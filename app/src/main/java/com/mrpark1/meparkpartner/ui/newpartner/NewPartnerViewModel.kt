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
import com.mrpark1.meparkpartner.data.model.partner.CheckAccountRequest
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

    val checkAccountMessage = MutableLiveData("")

    private val _formsFilled = MutableLiveData(false)
    val formsFilled: LiveData<Boolean> = _formsFilled

    private val _currentStatus = MutableLiveData<Status>()
    val currentStatus: LiveData<Status> = _currentStatus

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, e ->
        e.printStackTrace()
        when (e) {
            is UnknownHostException -> {
                _currentStatus.value = Status.ERROR_INTERNET
                Log.d("TEST@","UnknownHostException :: ${e.message}")
            }
            is JsonDataException -> {
                _currentStatus.value = Status.ERROR
                Log.d("TEST@","JsonDataException :: ${e.message}")
            }
            else ->{
                _currentStatus.value = Status.ERROR
                Log.d("TEST@","error :: ${e.localizedMessage}")
                Log.d("TEST@","error2 :: ${e}")
                Log.d("TEST@","error3 :: ${e.message}")
            }
        }
    }


    fun checkAccount(name: String, num: String){
        if (currentStatus.value == Status.LOADING) return
        _currentStatus.value = Status.LOADING

        val bankNum = getBankNum(selectedBank.value!!)

        Log.d("TEST@","ACCT_NM :: $name")
        Log.d("TEST@","BANK_CD :: $bankNum")
        Log.d("TEST@","SEARCH_ACCT_NO :: ${bankAccount.value!!}")
        Log.d("TEST@","ACNM_NO :: $num")
        viewModelScope.launch(coroutineExceptionHandler) {
            val response = withContext(Dispatchers.IO) {
                newPartnerRepository.checkAccount(
                    CheckAccountRequest(
                        ACCT_NM = name,
                        BANK_CD = bankNum,
                        SEARCH_ACCT_NO = bankAccount.value!!,
                        ACNM_NO = num
                    )
                )
            }

            when {
                response.isSuccessful -> {
                    checkAccountMessage.value = response.body()!!.message
                    _currentStatus.value = Status.NEWPART_ACCOUNT_CHECK
                }
                response.code() == 403 -> {
                    _currentStatus.value = Status.ERROR_EXPIRED
                }
                else -> {
                    checkAccountMessage.value = "정보가 정확하지 않습니다."
                    _currentStatus.value = Status.NEWPART_ACCOUNT_CHECK
                }
            }
        }
    }
    //파트너 생성 요청
    fun applyNewPartner() {
        if (currentStatus.value == Status.LOADING) return
        _currentStatus.value = Status.LOADING

        var photoBR = ""
        if(photoUri.value!=null){
            photoBR = imageUtil.uriResizeToBase64(photoUri.value!!)
        }

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
                        BRPhoto = photoBR
                    )
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
                    (!partnerBn.value.isNullOrBlank() && photoUri.value == null) ||
                    checkAccountMessage.value!="예금주 확인이 되었습니다.")
        }
    }





    fun getBankNum(bankName : String): String{
        var bankNum = ""
        when(bankName){
            "산업은행" -> bankNum ="002"
            "기업은행" -> bankNum ="003"
            "국민은행" -> bankNum ="004"
            "수협은행" -> bankNum ="007"
            "농협은행" -> bankNum ="011"
            "농협중앙회" -> bankNum ="012"
            "우리은행" -> bankNum ="020"
            "SC제일은행" -> bankNum ="023"
            "한국씨티" -> bankNum ="027"
            "대구은행" -> bankNum ="031"
            "부산은행" -> bankNum ="032"
            "광주은행" -> bankNum ="034"
            "제주은행" -> bankNum ="035"
            "전북은행" -> bankNum ="037"
            "경남은행" -> bankNum ="039"
            "새마을금고중앙회" -> bankNum ="045"
            "신협중앙회" -> bankNum ="048"
            "상호저축은행중앙회" -> bankNum ="050"
            "HSBC" -> bankNum ="054"
            "도이치" -> bankNum ="055"
            "JP모간체이스은행" -> bankNum ="057"
            "뱅크오브아메리카" -> bankNum ="060"
            "BNP파리바" -> bankNum ="061"
            "중국공상은행" -> bankNum ="062"
            "산림조합중앙회" -> bankNum ="064"
            "교통은행" -> bankNum ="066"
            "중국건설은행" -> bankNum ="067"
            "우정사업본부" -> bankNum ="071"
            "KEB하나은행" -> bankNum ="081"
            "신한은행" -> bankNum ="088"
            "K뱅크은행" -> bankNum ="089"
            "카카오뱅크" -> bankNum ="090"
            "유안타증권" -> bankNum ="209"
            "KB증권" -> bankNum ="218"
            "BNK투자증권" -> bankNum ="224"
            "KTB투자증권" -> bankNum ="227"
            "미래에셋대우" -> bankNum ="238"
            "삼성증권" -> bankNum ="240"
            "한국투자증권" -> bankNum ="243"
            "NH투자증권" -> bankNum ="247"
            "교보증권" -> bankNum ="261"
            "하이투자증권" -> bankNum ="262"
            "현대차증권" -> bankNum ="263"
            "키움증권" -> bankNum ="264"
            "이베스트투자증권" -> bankNum ="265"
            "SK증권" -> bankNum ="266"
            "대신증권" -> bankNum ="267"
            "한화투자증권" -> bankNum ="269"
            "하나금융투자" -> bankNum ="270"
            "신한금융투자" -> bankNum ="278"
            "DB금융투자" -> bankNum ="279"
            "유진투자증권" -> bankNum ="280"
            "메리츠종합금융" -> bankNum ="287"
            "바로투자증권" -> bankNum ="288"
            "부국증권" -> bankNum ="290"
            "신영증권" -> bankNum ="291"
            "케이프투자증권" -> bankNum ="292"
            "한국포스증권" -> bankNum ="294"
        }
        return bankNum
    }
}