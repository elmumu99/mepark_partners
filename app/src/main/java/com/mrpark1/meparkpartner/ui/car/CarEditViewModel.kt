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
class CarEditViewModel @Inject constructor(private val carEditRepository: CarEditRepositoryImpl) :
    ViewModel() {

    private lateinit var car: Car
    private var initialized = false

    val lp = MutableLiveData("")
    val enterTime = MutableLiveData("")
    val visitPlace = MutableLiveData("")

    //intent로 받아온 정보 초기화
    fun initialize(car: Car) {
        if (initialized) return
        this.car = car
        lp.value = car.LP
        enterTime.value = car.EnterTime
        visitPlace.value = car.VisitPlace
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

    //차량 정보 수정
    fun updateCar() {
        if (currentStatus.value == Status.LOADING) return
        _currentStatus.value = Status.LOADING

        viewModelScope.launch(coroutineExceptionHandler) {
            val response = withContext(Dispatchers.IO) {
                carEditRepository.updateEntranceAndExitCar(
                    UpdateEntranceAndExitCarRequest(
                        Mode = "1",
                        ParkingLN = car.ParkingLN,
                        CarID = car.CarID,
                        Penalty = car.Penalty,
                        Discount = car.Discount,
                        LP = lp.value!!,
                        Memo = car.Memo,
                        EnterDate = car.EnterDate,
                        Contact = car.Contact,
                        EnterTime = enterTime.value!!,
                        CarType = car.CarType,
                        VisitPlace = visitPlace.value!!
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
                !(lp.value.isNullOrBlank() || !Regex("^(?:\\d|[01]\\d|2[0-3]):[0-5]\\d\$")
                    .matches(enterTime.value!!))
        }
    }
}