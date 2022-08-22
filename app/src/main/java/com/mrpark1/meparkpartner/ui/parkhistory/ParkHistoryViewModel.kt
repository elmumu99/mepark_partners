package com.mrpark1.meparkpartner.ui.parkhistory

import android.util.Log
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrpark1.meparkpartner.data.model.common.Car
import com.mrpark1.meparkpartner.data.model.common.ParkingLot
import com.mrpark1.meparkpartner.data.model.parkinglot.car.GetParkHistoryRequest
import com.mrpark1.meparkpartner.data.repository.implementations.ParkHistoryRepositoryImpl
import com.mrpark1.meparkpartner.ui.Status
import com.squareup.moshi.JsonDataException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ParkHistoryViewModel @Inject constructor(private val parkHistoryRepository: ParkHistoryRepositoryImpl) :
    ViewModel() {

    lateinit var parkingLot: ParkingLot

    val cars = ObservableArrayList<Car>()

    private val cal = Calendar.getInstance()
    private val sdf = SimpleDateFormat("yyyy-MM-dd")
    private val _startDate = MutableLiveData(sdf.format(cal.timeInMillis))
    val startDate: LiveData<String> = _startDate
    private val _endDate = MutableLiveData(sdf.format(cal.timeInMillis))
    val endDate: LiveData<String> = _endDate

    private val _returnCount = MutableLiveData(0)
    val returnCount: LiveData<Int> = _returnCount
    private val _exitCount = MutableLiveData(0)
    val exitCount: LiveData<Int> = _exitCount

    private val _currentStatus = MutableLiveData<Status>()
    val currentStatus: LiveData<Status> = _currentStatus

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, e ->
        e.printStackTrace()
        when (e) {
            is UnknownHostException -> _currentStatus.value = Status.ERROR_INTERNET
            is JsonDataException -> {
                _currentStatus.value = Status.ERROR
                Log.d("TEST@","JsonDataException")
            }
            else -> {
                _currentStatus.value = Status.ERROR
                Log.d("TEST@","error :: $e")

            }
        }
    }

    fun getParkHistory() {
        if (currentStatus.value == Status.LOADING) return
        _currentStatus.value = Status.LOADING

        var endDateString = ""
        try{
            cal.time = sdf.parse(_endDate.value)
            cal.add(Calendar.DAY_OF_MONTH,1)
            endDateString = sdf.format(cal.time)
        }catch (e: ParseException){

            endDateString = _endDate.value!!
        }

        viewModelScope.launch(coroutineExceptionHandler) {
            val response = withContext(Dispatchers.IO) {
                parkHistoryRepository.getParkHistory(
                    GetParkHistoryRequest(
                        ParkingLN = parkingLot.ParkingLN,
                        StartDate = startDate.value!!,
                        EndDate = endDateString
                    )
                )
            }
            Log.d("TEST@","ParkingLN :: ${parkingLot.ParkingLN}")
            Log.d("TEST@","StartDate :: ${startDate.value!!}")
            Log.d("TEST@","EndDate :: ${endDateString}")
            when {
                response.isSuccessful -> {

                    val exitCars = response.body()!!.ExitCars
                    val returnCars = response.body()!!.ReturnCars
                    val result = exitCars + returnCars

                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)

                    _exitCount.value = exitCars.size
                    _returnCount.value = returnCars.size

                    for (car in result) {
                        val hour = car.TotalTime.toInt()/60
                        val min = car.TotalTime.toInt()%60


                        car.parkTime =
                            "${String.format("%02d", hour)} : ${String.format("%02d", min)}"


                        val finalFee =
                            car.Profit.toInt() + car.DefaultFee.toInt() - car.Discount.toInt() + car.Penalty.toInt()
                        car.finalFee = if (finalFee > 0) finalFee.toString() else "0"
                    }

                    cars.clear()
                    cars.addAll(result)
                    if (cars.isEmpty()) _currentStatus.value = Status.PARKHIS_NO_CARS
                    else _currentStatus.value = Status.SUCCESS
                }
                else -> {
                    _currentStatus.value = Status.ERROR
                }
            }
        }
    }

    fun setSelectedDate(startDate: String,endDate: String) {
        _startDate.value = startDate


        _endDate.value = endDate
        getParkHistory()
    }
}