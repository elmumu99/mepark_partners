package com.mrpark1.meparkpartner.ui.park

import android.util.Log
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.data.model.common.Car
import com.mrpark1.meparkpartner.data.model.common.ParkingLot
import com.mrpark1.meparkpartner.data.model.parkinglot.car.GetParkedCarsRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.car.UpdateEntranceAndExitCarRequest
import com.mrpark1.meparkpartner.data.repository.implementations.ParkRepositoryImpl
import com.mrpark1.meparkpartner.ui.Status
import com.squareup.moshi.JsonDataException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ParkViewModel @Inject constructor(private val parkRepository: ParkRepositoryImpl) :
    ViewModel() {

    lateinit var parkingLot: ParkingLot

    //차량 목록, 검색된 차량 목록
    val cars = ObservableArrayList<Car>()
    val searchedCars = ObservableArrayList<Car>()

    val search = MutableLiveData("")

    //서버시간
    private val _serverDateTime = MutableLiveData("")
    val serverDateTime: LiveData<String> = _serverDateTime

    private val _enterCount = MutableLiveData(0)
    val enterCount: LiveData<Int> = _enterCount
    private val _exitCount = MutableLiveData(0)
    val exitCount: LiveData<Int> = _exitCount
    private val _regularCount = MutableLiveData(0)
    val regularCount: LiveData<Int> = _regularCount

    private val _currentStatus = MutableLiveData<Status>()
    val currentStatus: LiveData<Status> = _currentStatus
    var errorMessage = ""

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, e ->
        e.printStackTrace()
        when (e) {
            is UnknownHostException -> {
                _currentStatus.value = Status.ERROR_INTERNET
                Log.d("TEST@","UnknownHostException")
            }
            is JsonDataException -> {
                _currentStatus.value = Status.ERROR
                Log.d("TEST@","JsonDataException")
            }
            else ->{
                _currentStatus.value = Status.ERROR
                Log.d("TEST@","e :: $e")
            }
        }
    }

    //입차 차량 조회
    fun getParkedCars() {
        if (currentStatus.value == Status.LOADING) return
        _currentStatus.value = Status.LOADING

        viewModelScope.launch(coroutineExceptionHandler) {
            val response = withContext(Dispatchers.IO) {
                parkRepository.getParkedCars(
                    GetParkedCarsRequest(
                        ParkingLN = parkingLot.ParkingLN
                    )
                )
            }
            when {
                response.isSuccessful -> {
                    //서버시간과 차량의 입차시간 비교, 금액 산정
                    val result = response.body()!!.Cars
                    val serverTime = response.body()!!.NowDateTime
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)
                    val dateFormatShow = SimpleDateFormat("yyyy년 MM월 dd일 \naa hh:mm", Locale.KOREA)
                    _serverDateTime.value = dateFormatShow.format(dateFormat.parse(serverTime)!!)

                    _enterCount.value = 0
                    _exitCount.value = 0
                    _regularCount.value = 0

                    for (car in result) {
                        val hour = car.TotalTime.toInt()/60
                        val min = car.TotalTime.toInt()%60
                        car.parkTime =
                            "${String.format("%02d", hour)} : ${String.format("%02d", min)}"

                        val finalFee =
                            car.Profit.toInt() + car.DefaultFee.toInt() - car.Discount.toInt() + car.Penalty.toInt()
                        car.finalFee = if (finalFee > 0) finalFee.toString() else "0"

                        //입차/출차/정기주차 댓수 기록
                        when {
                            car.VisitPlace == "일주차" || car.VisitPlace == "월주차" -> _regularCount.value =
                                _regularCount.value!! + 1
                            car.Status == "Enter" -> _enterCount.value = _enterCount.value!! + 1
                            car.Status == "Paid" -> _exitCount.value = _exitCount.value!! + 1
                        }
                    }

                    cars.clear()
                    cars.addAll(response.body()!!.Cars)
                    refreshSearchedCars()
                    _currentStatus.value = Status.SUCCESS
                }
                else -> {
                    _currentStatus.value = Status.ERROR
                }
            }
        }
    }

    //차량 출차
    fun exitCar(carId: String) {
        if (currentStatus.value == Status.LOADING) return
        _currentStatus.value = Status.LOADING

        Log.d("TEST@","ParkingLN = ${parkingLot.ParkingLN}")
        Log.d("TEST@","CarID = ${carId}")
        viewModelScope.launch(coroutineExceptionHandler) {
            val response = withContext(Dispatchers.IO) {
                parkRepository.updateEntranceAndExitCar(
                    UpdateEntranceAndExitCarRequest(
                        Mode = "3",
                        ParkingLN = parkingLot.ParkingLN,
                        CarID = carId,
                        Payment = "1" // 0 이면 미결제 , 1이면 현금결제 , 2이면 간편결제
                    )
                )
            }
            when {
                response.isSuccessful -> {
                    _currentStatus.value = Status.PARK_EXIT_SUCCESS
                    getParkedCars()
                }
                else -> {
                    errorMessage = response.errorBody()?.string()!!
                    _currentStatus.value = Status.ERROR
                }
            }
        }
    }

    //차량번호로 검색
    fun refreshSearchedCars() {
        viewModelScope.launch {
            delay(100)
            searchedCars.clear()
            searchedCars.addAll(cars.filter { it.LP.contains(search.value!!) })
        }
    }
}