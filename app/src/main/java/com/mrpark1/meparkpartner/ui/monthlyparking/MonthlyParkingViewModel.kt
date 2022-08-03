package com.mrpark1.meparkpartner.ui.monthlyparking

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mrpark1.meparkpartner.ui.Status
import com.squareup.moshi.JsonDataException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class MonthlyParkingViewModel @Inject constructor(): ViewModel() {
    //현재 상태 저장
    private val _currentStatus = MutableLiveData<Status>()
    val currentStatus: LiveData<Status> = _currentStatus

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


}