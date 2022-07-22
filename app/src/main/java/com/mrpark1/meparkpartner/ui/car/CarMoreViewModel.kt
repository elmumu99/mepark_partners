package com.mrpark1.meparkpartner.ui.car

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrpark1.meparkpartner.data.model.common.Car
import com.mrpark1.meparkpartner.data.model.parkinglot.car.UpdateEntranceAndExitCarRequest
import com.mrpark1.meparkpartner.data.repository.implementations.CarEditRepositoryImpl
import com.mrpark1.meparkpartner.ui.Status
import com.squareup.moshi.JsonDataException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class CarMoreViewModel @Inject constructor(private val carEditRepository: CarEditRepositoryImpl) :
    ViewModel() {

    private lateinit var car: Car
    private var initialized = false

    val discount = MutableLiveData("")
    val penalty = MutableLiveData("")

    //intent로 받아온 정보 초기화
    fun initialize(car: Car) {
        if (initialized) return
        this.car = car
        discount.value = car.Discount
        penalty.value = car.Penalty
        initialized = true
    }

    private val _currentStatus = MutableLiveData<Status>()
    val currentStatus: LiveData<Status> = _currentStatus

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, e ->
        e.printStackTrace()
        when (e) {
            is UnknownHostException -> _currentStatus.value = Status.ERROR_INTERNET
            is JsonDataException -> _currentStatus.value = Status.ERROR
        }
    }

    //할인 및 추가금액 수정
    fun setDiscountPenalty() {
        if (currentStatus.value == Status.LOADING) return
        _currentStatus.value = Status.LOADING

        viewModelScope.launch(coroutineExceptionHandler) {
            val response = withContext(Dispatchers.IO) {
                carEditRepository.updateEntranceAndExitCar(
                    UpdateEntranceAndExitCarRequest(
                        Mode = "1",
                        ParkingLN = car.ParkingLN,
                        CarID = car.CarID,
                        Penalty = penalty.value!!,
                        Discount = discount.value!!,
                        LP = car.LP,
                        Memo = car.Memo,
                        EnterDate = car.EnterDate,
                        Contact = car.Contact,
                        EnterTime = car.EnterTime,
                        CarType = car.CarType,
                        VisitPlace = car.VisitPlace
                    )
                )
            }
            when {
                response.isSuccessful -> {
                    _currentStatus.value = Status.SUCCESS
                }
                else -> {
                    _currentStatus.value = Status.ERROR
                }
            }
        }
    }

    //회차
    fun returnCar() {
        if (currentStatus.value == Status.LOADING) return
        _currentStatus.value = Status.LOADING

        viewModelScope.launch(coroutineExceptionHandler) {
            val response = withContext(Dispatchers.IO) {
                carEditRepository.updateEntranceAndExitCar(
                    UpdateEntranceAndExitCarRequest(
                        Mode = "4",
                        ParkingLN = car.ParkingLN,
                        CarID = car.CarID
                    )
                )
            }
            when {
                response.isSuccessful -> {
                    _currentStatus.value = Status.SUCCESS
                }
                else -> {
                    _currentStatus.value = Status.ERROR
                }
            }
        }
    }

    private val _formsFilled = MutableLiveData(false)
    val formsFilled: LiveData<Boolean> = _formsFilled

    fun checkFormsFilled() {
        viewModelScope.launch {
            delay(100)
            _formsFilled.value =
                !(discount.value.isNullOrBlank() || penalty.value.isNullOrBlank())
        }
    }
}