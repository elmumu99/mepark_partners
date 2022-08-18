package com.mrpark1.meparkpartner.ui.monthlyparking

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrpark1.meparkpartner.data.model.common.MonthParkedCar
import com.mrpark1.meparkpartner.data.model.parkinglot.car.AddMonthParkedCarRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.car.GetMonthParkedCarsRequest
import com.mrpark1.meparkpartner.data.repository.implementations.MonthParkRepositoryImpl
import com.mrpark1.meparkpartner.ui.Status
import com.mrpark1.meparkpartner.util.Constants
import com.squareup.moshi.JsonDataException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class MonthlyParkingViewModel @Inject constructor(
    private val monthParkRepository: MonthParkRepositoryImpl
): ViewModel() {
    //현재 상태 저장
    private val _currentStatus = MutableLiveData<Status>()
    val currentStatus: LiveData<Status> = _currentStatus

    val mode = MutableLiveData("1")

    val carListData = arrayListOf<MonthParkedCar>()
    val carList = MutableLiveData<ArrayList<MonthParkedCar>>()

    val carDoneListData = arrayListOf<MonthParkedCar>()
    val carDoneList = MutableLiveData<ArrayList<MonthParkedCar>>()

    //코루틴 내에서 에러 발생 시 캐치 (인터넷 오류 등 분기처리)
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, e ->
        e.printStackTrace()
        Log.d("TEST@","MonthlyParkingViewModel :: $e")
        when (e) {
            is UnknownHostException -> _currentStatus.value = Status.ERROR_INTERNET
            is JsonDataException -> {
                _currentStatus.value = Status.ERROR
            }
            else -> {
                _currentStatus.value = Status.ERROR
            }
        }
    }

    fun setMode(mode: String){
        this.mode.value = mode
    }

    fun getMonthParkedCars(){
        if (currentStatus.value == Status.LOADING) return
        _currentStatus.value = Status.LOADING




        viewModelScope.launch(coroutineExceptionHandler) {
            val response = withContext(Dispatchers.IO) {
                monthParkRepository.getMonthParkedCars(
                    GetMonthParkedCarsRequest(
                        ParkingLN = Constants.selectedParkingLot.ParkingLN,
                        Mode = mode.value!!
                    )
                )
            }


            when {
                response.isSuccessful -> {
                    if(mode.value=="1"){
                        carListData.clear()
                        carListData.addAll(response.body()!!.CarResult)
                        carList.value = carListData
                    }else{
                        carDoneListData.clear()
                        carDoneListData.addAll(response.body()!!.CarResult)
                        carDoneList.value = carDoneListData
                    }

                    _currentStatus.value = Status.SUCCESS
                }
                response.code() == 403 -> {
                    _currentStatus.value = Status.ERROR_EXPIRED
                }
                else -> {
                    _currentStatus.value = Status.ERROR
                }
            }
        }
    }

}