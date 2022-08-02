package com.mrpark1.meparkpartner.ui.newparkinglot

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.mrpark1.meparkpartner.data.model.common.ParkingLot
import com.mrpark1.meparkpartner.data.model.common.VisitPlace
import com.mrpark1.meparkpartner.data.model.parkinglot.AddMyParkingLotRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.AddParkingLotQrcodeRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.RemoveMyParkingLotRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.UpdateMyParkingLotRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.visitplace.AddMyVisitPlacesRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.visitplace.RemoveMyVisitPlaceRequest
import com.mrpark1.meparkpartner.data.repository.implementations.NewParkingLotRepositoryImpl
import com.mrpark1.meparkpartner.ui.Status
import com.mrpark1.meparkpartner.util.Constants.TAG
import com.mrpark1.meparkpartner.util.ImageUtil
import com.squareup.moshi.JsonDataException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class NewParkingLotViewModel @Inject constructor(
    private val imageUtil: ImageUtil, //이미지 base64 변환 유틸
    private val newParkingLotRepository: NewParkingLotRepositoryImpl
) :
    ViewModel() {

    private var initialized = false
    private lateinit var editParkingLot: ParkingLot

    val name = MutableLiveData("")
    val address = MutableLiveData("")
    val addressDetail = MutableLiveData("")
    val other = MutableLiveData("")
    val day = MutableLiveData("")
    val month = MutableLiveData("")
    val spaceAll = MutableLiveData("")
    val spaceMax = MutableLiveData("")
    val photoUri = MutableLiveData<Uri>()
    val imageUrl = MutableLiveData("")

    val isEdit = MutableLiveData(false)

    val visitPlaces = ObservableArrayList<VisitPlace>()
    private val deletedVisitPlaces = arrayListOf<VisitPlace>()
    val insurance = MutableLiveData("")

    //주차장 수정일 경우 기존 정보로 필드 초기화
    fun initialize(parkingLot: ParkingLot) {
        if (initialized) return
        editParkingLot = parkingLot
        isEdit.value = true
        name.value = parkingLot.Name
        other.value = parkingLot.OtherPlaceFee
        day.value = parkingLot.DayParkFee
        month.value = parkingLot.MonthParkFee
        spaceAll.value = parkingLot.Spaces
        spaceMax.value = parkingLot.MinLeftSpaces
        visitPlaces.clear()
        visitPlaces.addAll(parkingLot.VisitPlaces.filter {
            it.PlaceName != "기타" && it.PlaceName != "일주차" && it.PlaceName != "월주차"
        })
        insurance.value = parkingLot.Insurance
        address.value = parkingLot.Address
        initialized = true
        imageUrl.value = parkingLot.ImgUrl
    }

    private val _formsFilled = MutableLiveData(false)
    val formsFilled: LiveData<Boolean> = _formsFilled

    private val _currentStatus = MutableLiveData<Status>()
    val currentStatus: LiveData<Status> = _currentStatus

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, e ->
        e.printStackTrace()
        when (e) {
            is UnknownHostException -> _currentStatus.value = Status.ERROR_INTERNET
            is JsonDataException -> _currentStatus.value = Status.ERROR
        }
    }

    //이미지피커 콜백 처리
    fun getImageFromPickerResult(result: ActivityResult) {
        val resultCode = result.resultCode
        val data = result.data

        when (resultCode) {
            Activity.RESULT_OK -> {
                photoUri.value = data?.data!!
            }
            ImagePicker.RESULT_ERROR -> _currentStatus.value = Status.NEWPARK_ERROR_PROFILE
            else -> _currentStatus.value = Status.INIT
        }
    }

    //주차장 추가
    fun addMyParkingLot() {
        if (currentStatus.value == Status.LOADING) return
        _currentStatus.value = Status.LOADING

        //주차장 수정일 경우 updateMyParkingLot() 실행
        if (isEdit.value!!) {
            updateMyParkingLot()
            return
        }
        var photoBR = ""
        if(photoUri.value!=null){
            photoBR = imageUtil.uriResizeToBase64(photoUri.value!!)
        }
        viewModelScope.launch(coroutineExceptionHandler) {
            val response = withContext(Dispatchers.IO) {
                newParkingLotRepository.addMyParkingLot(
                    AddMyParkingLotRequest(
                        Name = name.value!!,
                        Address = address.value!! + if (addressDetail.value.isNullOrBlank()) "" else (" " + addressDetail.value!!),
                        Insurance = insurance.value!!,
                        OtherPlaceFee = other.value!!,
                        DayParkFee = day.value!!,
                        MonthParkFee = month.value!!,
                        Spaces = spaceAll.value!!,
                        MinLeftSpaces = spaceMax.value!!,
                        Photo = photoBR
                    )
                )
            }
            when {
                response.isSuccessful -> {
                    //주차장 추가 시 얻은 주차장 고유번호 이용 방문지 추가
                    val parkingLN = response.body()!!.ParkingLN
                    for (visitPlace in visitPlaces) {
                        if (!addMyVisitPlace(visitPlace, parkingLN)) {
                            _currentStatus.value = Status.NEWPARK_ERROR_VISITPLACE
                            return@launch
                        }
                    }
                    //주차장 QR 이미지 추가
                    if (!addParkingLotQrcode(parkingLN)) {
                        _currentStatus.value = Status.NEWPARK_ERROR_QR
                        return@launch
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

    //주차장 수정
    private fun updateMyParkingLot() {
        _currentStatus.value = Status.LOADING

        var photoBR = ""
        if(photoUri.value!=null){
            photoBR = imageUtil.uriResizeToBase64(photoUri.value!!)
        }

        viewModelScope.launch(coroutineExceptionHandler) {
            val response = withContext(Dispatchers.IO) {
                newParkingLotRepository.updateMyParkingLot(
                    UpdateMyParkingLotRequest(
                        ParkingLN = editParkingLot.ParkingLN,
                        Name = name.value!!,
                        Address = address.value!! + if (addressDetail.value.isNullOrBlank()) "" else (" " + addressDetail.value!!),
                        Insurance = insurance.value!!,
                        OtherPlaceFee = other.value!!,
                        DayParkFee = day.value!!,
                        MonthParkFee = month.value!!,
                        Spaces = spaceAll.value!!,
                        MinLeftSpaces = spaceMax.value!!,
                        Photo = photoBR,
                        ParkEnabled = editParkingLot.ParkEnabled
                    )
                )
            }
            when {
                response.isSuccessful -> {
                    //삭제된 방문지들 반영
                    for (visitPlace in deletedVisitPlaces) {
                        if (!removeMyVisitPlace(visitPlace, editParkingLot.ParkingLN)) {
                            _currentStatus.value = Status.NEWPARK_ERROR_VISITPLACE
                            return@launch
                        }
                    }
                    //추가/수정된 방문지들 반영
                    for (visitPlace in visitPlaces) {
                        if (!addMyVisitPlace(visitPlace, editParkingLot.ParkingLN)) {
                            _currentStatus.value = Status.NEWPARK_ERROR_VISITPLACE
                            return@launch
                        }
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

    //방문지 추가/수정(같은 이름의 방문지 추가 시 수정)
    private suspend fun addMyVisitPlace(visitPlace: VisitPlace, parkingLN: String): Boolean {
        _currentStatus.value = Status.LOADING

        val response = withContext(Dispatchers.IO) {
            newParkingLotRepository.addMyVisitPlace(
                AddMyVisitPlacesRequest(
                    ParkingLN = parkingLN,
                    PlaceName = visitPlace.PlaceName,
                    FreeTime = visitPlace.FreeTime,
                    DefaultTime = visitPlace.DefaultTime,
                    DefaultFee = visitPlace.DefaultFee,
                    TenMinutesFee = visitPlace.TenMinutesFee
                )
            )
        }

        return when {
            response.isSuccessful -> true
            else -> false
        }
    }

    //방문지 삭제
    private suspend fun removeMyVisitPlace(visitPlace: VisitPlace, parkingLN: String): Boolean {
        _currentStatus.value = Status.LOADING

        val response = withContext(Dispatchers.IO) {
            newParkingLotRepository.removeMyVisitPlace(
                RemoveMyVisitPlaceRequest(
                    ParkingLN = parkingLN,
                    PlaceName = visitPlace.PlaceName
                )
            )
        }

        return when {
            response.isSuccessful -> true
            else -> false
        }
    }

    //주차장 QR 이미지 업로드
    private suspend fun addParkingLotQrcode(parkingLN: String): Boolean {
        _currentStatus.value = Status.LOADING

        val qr = imageUtil.bitmapToBase64(getQrCodeBitmap(parkingLN))
        Log.d(TAG, "addParkingLotQrcode: $qr")

        val response = withContext(Dispatchers.IO) {
            newParkingLotRepository.addParkingLotQrcode(
                AddParkingLotQrcodeRequest(
                    ParkingLN = parkingLN,
                    Photo = qr
                )
            )
        }

        return when {
            response.isSuccessful -> true
            else -> false
        }
    }

    //QR코드 비트맵 찍기
    private fun getQrCodeBitmap(text: String): Bitmap {
        val size = 512 //pixels
        val hints = hashMapOf<EncodeHintType, Int>().also {
            it[EncodeHintType.MARGIN] = 1
        } // Make the QR code buffer border narrower
        val bits = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size)
        return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also {
            for (x in 0 until size) {
                for (y in 0 until size) {
                    it.setPixel(x, y, if (bits[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        }
    }

    //방문지 수정 액티비티 콜백
    fun updateVisitPlace(result: ActivityResult) {
        val resultCode = result.resultCode
        val data = result.data ?: return

        when (resultCode) {
            Activity.RESULT_OK -> {
                //방문지가 삭제되었을 경우 반영
                val deletePlace = data.getStringExtra("deletePlace")

                if(deletePlace!=null) {
                    if (visitPlaces.size >= 2) { //방문지가 두개 이상일경우
                        deletePlace.let { placeName ->
                            val index = visitPlaces.indexOfFirst { it.PlaceName == placeName }
                            deletedVisitPlaces.add(visitPlaces[index])
                            visitPlaces.removeAt(index)
                            return@updateVisitPlace
                        }
                    }else{ //방문지가 1개 이하일경우
                        _currentStatus.value = Status.NEWPARK_NEED_VISITPLACE
                        return
                    }
                }

                //방문지가 수정되었을 경우 반영
                val visitPlace = data.getSerializableExtra("visitPlace") as VisitPlace
                if (data.getBooleanExtra("isEdit", false)) {
                    visitPlaces[visitPlaces.indexOfFirst { it.PlaceName == visitPlace.PlaceName }] =
                        visitPlace
                } else {
                    visitPlaces.add(visitPlace)
                }
            }
        }
    }

    //주소 입력
    fun updateAddress(result: ActivityResult) {
        val resultCode = result.resultCode
        val data = result.data ?: return

        when (resultCode) {
            Activity.RESULT_OK -> {
                data.getStringExtra("address")?.let { add ->
                    address.value = add
                }
            }
        }
    }

    //보험 선택
    fun setInsurance(selected: String) {
        insurance.value = selected
        checkFormsFilled()
    }

    fun updateInsurance(result: ActivityResult) {
        val resultCode = result.resultCode
        val data = result.data ?: return

        when (resultCode) {
            Activity.RESULT_OK -> {
                data.getStringExtra("selectedPlan")?.let { selectedPlan ->
                    insurance.value = selectedPlan
                    return@updateInsurance
                }
            }
        }
    }

    fun checkFormsFilled() {
        viewModelScope.launch {
            delay(100)
            _formsFilled.value =
                !(name.value.isNullOrBlank() || address.value.isNullOrBlank() || other.value.isNullOrBlank() || spaceAll.value.isNullOrBlank()
                        || spaceMax.value.isNullOrBlank() || insurance.value.isNullOrBlank())

        }
    }

    fun deleteParkingLot(){
        if (currentStatus.value == Status.LOADING) return
        _currentStatus.value = Status.LOADING

        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                newParkingLotRepository.removeMyParkingLot(
                    RemoveMyParkingLotRequest(editParkingLot.ParkingLN)
                )
            }

            when {
                response.isSuccessful -> {
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