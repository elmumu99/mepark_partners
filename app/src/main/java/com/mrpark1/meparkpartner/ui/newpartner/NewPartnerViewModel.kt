package com.mrpark1.meparkpartner.ui.newpartner

import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.dhaval2404.imagepicker.ImagePicker
import com.mrpark1.meparkpartner.data.model.partner.ApplyNewPartnerRequest
import com.mrpark1.meparkpartner.data.repository.implementations.NewPartnerRepositoryImpl
import com.mrpark1.meparkpartner.ui.Status
import com.mrpark1.meparkpartner.util.ImageUtil
import com.squareup.moshi.JsonDataException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class NewPartnerViewModel @Inject constructor(
    private val imageUtil: ImageUtil,
    private val newPartnerRepository: NewPartnerRepositoryImpl
) : ViewModel() {

    lateinit var username: String

    val partnerName = MutableLiveData("")
    val contact = MutableLiveData("")
    val partnerBn = MutableLiveData("")
    val photoUri = MutableLiveData<Uri>()
    private val _photoName = MutableLiveData("")
    val photoName: LiveData<String> = _photoName
    val selectedBank = MutableLiveData("")
    val bankAccount = MutableLiveData("")

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

    //파트너 생성 요청
    fun applyNewPartner() {
        if (currentStatus.value == Status.LOADING) return
        _currentStatus.value = Status.LOADING

        viewModelScope.launch(coroutineExceptionHandler) {
            val response = withContext(Dispatchers.IO) {
                newPartnerRepository.applyNewPartner(
                    ApplyNewPartnerRequest(
                        PartnerBN = if (partnerBn.value.isNullOrBlank()) "None" else partnerBn.value!!,
                        PhoneNumber = contact.value!!,
                        PartnerName = partnerName.value!!,
                        OwnerName = username,
                        BankAccount = bankAccount.value!!,
                        BankName = selectedBank.value!!,
                        BRPhoto = imageUtil.uriResizeToBase64(photoUri.value!!)
                    )
                )
            }

            Log.d("TEST@","image64:: ${imageUtil.uriResizeToBase64(photoUri.value!!)}")

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

    //이미지피커 콜백 처리
    fun getImageFromPickerResult(result: ActivityResult) {
        val resultCode = result.resultCode
        val data = result.data

        when (resultCode) {
            Activity.RESULT_OK -> {
                val uri = data?.data!!
                photoUri.value = uri
                _photoName.value = uri.pathSegments.last()
                _currentStatus.value = Status.NEWPART_PHOTO_PICK
            }
            ImagePicker.RESULT_ERROR -> _currentStatus.value = Status.NEWPART_ERROR_PHOTO
            else -> _currentStatus.value = Status.INIT
        }
    }

    fun checkFormsFilled() {
        viewModelScope.launch {
            delay(100)
            _formsFilled.value = !(partnerName.value.isNullOrBlank() ||
                    contact.value.isNullOrBlank() ||
                    bankAccount.value.isNullOrBlank() ||
                    selectedBank.value.isNullOrBlank() ||
                    (!partnerBn.value.isNullOrBlank() && photoUri.value == null))
        }
    }
}