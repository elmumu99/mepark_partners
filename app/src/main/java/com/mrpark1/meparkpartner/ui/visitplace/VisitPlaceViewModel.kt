package com.mrpark1.meparkpartner.ui.visitplace

import androidx.lifecycle.*
import com.mrpark1.meparkpartner.data.model.common.VisitPlace
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VisitPlaceViewModel(editPlace: VisitPlace?) : ViewModel() {

    //방문지 수정 시 필드 값 초기화
    val name = MutableLiveData(editPlace?.PlaceName ?: "")
    val freeTime = MutableLiveData(editPlace?.FreeTime ?: "")
    val defaultTime = MutableLiveData(editPlace?.DefaultTime ?: "")
    val defaultFee = MutableLiveData(editPlace?.DefaultFee ?: "")
    val tenMinutesFee = MutableLiveData(editPlace?.TenMinutesFee ?: "")

    private val _formsFilled = MutableLiveData(false)
    val formsFilled: LiveData<Boolean> = _formsFilled

    fun checkFormsFilled() {
        viewModelScope.launch {
            delay(100)
            _formsFilled.value = !(name.value.isNullOrBlank() ||
                    freeTime.value.isNullOrBlank() ||
                    defaultTime.value.isNullOrBlank() ||
                    defaultFee.value.isNullOrBlank() ||
                    tenMinutesFee.value.isNullOrBlank())
        }
    }

    //입력된 값으로 방문지 객체 만듬
    fun getVisitPlace() = VisitPlace(
        PlaceName = name.value!!,
        FreeTime = freeTime.value!!,
        DefaultTime = defaultTime.value!!,
        DefaultFee = defaultFee.value!!,
        TenMinutesFee = tenMinutesFee.value!!,
        ParkingLN = ""
    )

    //인자를 가진 ViewModel 사용하기 위함
    class VisitPlaceViewModelFactory(private val editPlace: VisitPlace?) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            modelClass.getConstructor(VisitPlace::class.java).newInstance(editPlace)
    }
}