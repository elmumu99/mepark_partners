package com.mrpark1.meparkpartner.ui.enter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.data.model.common.ParkingLot
import com.mrpark1.meparkpartner.data.model.parkinglot.car.AddEntranceAndExitCarRequest
import com.mrpark1.meparkpartner.data.repository.implementations.EnterRepositoryImpl
import com.mrpark1.meparkpartner.ui.Status
import com.squareup.moshi.JsonDataException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class EnterViewModel @Inject constructor(private val enterRepository: EnterRepositoryImpl) :
    ViewModel() {

    lateinit var parkingLot: ParkingLot

    val lp = MutableLiveData("")
    val visitPlace = MutableLiveData("")
    val contact = MutableLiveData("")
    val memo = MutableLiveData("")
    val carType = MutableLiveData(R.id.rb_enter_small)

    private val _currentStatus = MutableLiveData<Status>()
    val currentStatus: LiveData<Status> = _currentStatus

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, e ->
        e.printStackTrace()
        when (e) {
            is UnknownHostException -> _currentStatus.value = Status.ERROR_INTERNET
            is JsonDataException -> _currentStatus.value = Status.ERROR
        }
    }

    //차량 입차
    fun addCar() {
        if (currentStatus.value == Status.LOADING) return
        _currentStatus.value = Status.LOADING

        viewModelScope.launch(coroutineExceptionHandler) {
            val response = withContext(Dispatchers.IO) {
                enterRepository.addEntranceAndExitCar(
                    AddEntranceAndExitCarRequest(
                        ParkingLN = parkingLot.ParkingLN,
                        LP = lp.value!!,
                        CarType = if (carType.value!! == R.id.rb_enter_small) "Small" else "Big",
                        VisitPlace = visitPlace.value!!,
                        Contact = contact.value!!,
                        Memo = memo.value!!
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
                !(lp.value.isNullOrBlank() || visitPlace.value.isNullOrBlank())
        }
    }

}