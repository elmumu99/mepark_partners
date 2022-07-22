package com.mrpark1.meparkpartner.ui.parkhistory

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
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ParkHistoryViewModel @Inject constructor(private val parkHistoryRepository: ParkHistoryRepositoryImpl) :
    ViewModel() {

    lateinit var parkingLot: ParkingLot

    val cars = ObservableArrayList<Car>()

    private val cal = Calendar.getInstance()
    private val _selectedDate = MutableLiveData(
        "${cal.get(Calendar.YEAR)}-" +
                "${String.format("%02d", cal.get(Calendar.MONTH) + 1)}-" +
                String.format("%02d", cal.get(Calendar.DAY_OF_MONTH))
    )
    val selectedDate: LiveData<String> = _selectedDate

    private val _serverDateTime = MutableLiveData("")
    val serverDateTime: LiveData<String> = _serverDateTime

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
            is JsonDataException -> _currentStatus.value = Status.ERROR
        }
    }

    fun getParkHistory() {
        if (currentStatus.value == Status.LOADING) return
        _currentStatus.value = Status.LOADING

        viewModelScope.launch(coroutineExceptionHandler) {
            val response = withContext(Dispatchers.IO) {
                parkHistoryRepository.getParkHistory(
                    GetParkHistoryRequest(
                        ParkingLN = parkingLot.ParkingLN,
                        Date = selectedDate.value!!
                    )
                )
            }
            when {
                response.isSuccessful -> {
                    val exitCars = response.body()!!.ExitCars
                    val returnCars = response.body()!!.ReturnCars
                    val result = exitCars + returnCars

                    val serverTime = response.body()!!.NowDateTime
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)
                    val dateFormatShow = SimpleDateFormat("yyyy년 MM월 dd일 aa hh:mm", Locale.KOREA)
                    _serverDateTime.value = dateFormatShow.format(dateFormat.parse(serverTime)!!)

                    _exitCount.value = exitCars.size
                    _returnCount.value = returnCars.size

                    for (car in result) {
                        val enterDate = dateFormat.parse("${car.EnterDate} ${car.EnterTime}:00")
                        val exitDate =
                            if (car.Status == "Exit") dateFormat.parse("${car.ExitDate} ${car.ExitTime}:00")
                            else dateFormat.parse("${car.ReturnDate} ${car.ReturnTime}:00")

                        val diff = exitDate!!.time - enterDate!!.time
                        val hour = diff / (1000 * 60 * 60)
                        val min = diff / (1000 * 60) % 60
                        car.parkTime =
                            "${String.format("%02d", hour)} : ${String.format("%02d", min)}"

                        val visitPlace =
                            parkingLot.VisitPlaces.first { it.PlaceName == car.VisitPlace }
                        car.defaultFee = visitPlace.DefaultFee
                        if (car.defaultFee.isBlank()) car.defaultFee = "0"

                        val price =
                            visitPlace.TenMinutesFee.toInt() * if (visitPlace.PlaceName != "기타") {
                                ((hour * 60 + min - visitPlace.FreeTime.toInt() - visitPlace.DefaultTime.toInt()) / 10)
                            } else {
                                ((hour * 60 + min) / 10)
                            }
                        car.timeFee = if (price > 0) price.toString() else "0"

                        val finalFee =
                            car.timeFee.toInt() + car.defaultFee.toInt() - car.Discount.toInt() + car.Penalty.toInt()
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

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
        getParkHistory()
    }
}