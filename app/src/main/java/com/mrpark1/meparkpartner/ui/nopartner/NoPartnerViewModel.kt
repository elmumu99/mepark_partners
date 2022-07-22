package com.mrpark1.meparkpartner.ui.nopartner

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrpark1.meparkpartner.data.model.common.User
import com.mrpark1.meparkpartner.data.model.user.ApplyMyPartnerRequest
import com.mrpark1.meparkpartner.data.model.user.GetMyInfoRequest
import com.mrpark1.meparkpartner.data.model.user.GetMyPartnerInvitationRequest
import com.mrpark1.meparkpartner.data.model.user.GetMyPartnerInvitationResponse
import com.mrpark1.meparkpartner.data.repository.implementations.NoPartnerRepositoryImpl
import com.mrpark1.meparkpartner.ui.Status
import com.squareup.moshi.JsonDataException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class NoPartnerViewModel @Inject constructor(private val noPartnerRepository: NoPartnerRepositoryImpl) :
    ViewModel() {

    val user = MutableLiveData<User>()
    val invitation = MutableLiveData<MutableList<GetMyPartnerInvitationResponse>>()
    val invitation_name = MutableLiveData("")
    val start_day = MutableLiveData("")
    val salary = MutableLiveData("")
    val position = MutableLiveData(0)
    val isVisible = MutableLiveData(true)

    private val _currentStatus = MutableLiveData<Status>()
    val currentStatus: LiveData<Status>
        get() = _currentStatus

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, e ->
        e.printStackTrace()
        when (e) {
            is UnknownHostException -> _currentStatus.value = Status.ERROR_INTERNET
            is JsonDataException -> {
                _currentStatus.value = Status.ERROR
            }
        }
    }

    //파트너 소속 여부 조회
    fun refreshUser() {
        _currentStatus.value = Status.LOADING
        viewModelScope.launch(coroutineExceptionHandler) {
            getMyInfo()
            getMyPartnerInvitation()
        }
    }

    //유저 정보 가져오기
    private suspend fun getMyInfo() {
        val response = withContext(Dispatchers.IO) {
            noPartnerRepository.getMyInfo(GetMyInfoRequest())
        }
        when {
            response.isSuccessful -> {
                val body = response.body()!!
                user.value = body

                _currentStatus.value = when {
                    body.PartnerStatus == "Normal" -> Status.SUCCESS
                    //body.Status == "Pending" -> Status.NOPART_PENDING
                    body.PartnerStatus == "Pending" -> Status.SUCCESS
                    //TODO: for test
                    //원래 Pending일 경우 파트너 승인 대기중이나 해당 API 미구현으로 임의로 통과되게 했음
                    body.Invitation != null -> Status.NOPART_INVITED
                    else -> Status.NOPART_NO_PARTNER
                }
            }
            response.code() == 403 -> {
                _currentStatus.value = Status.ERROR_EXPIRED
            }
            else -> _currentStatus.value = Status.ERROR
        }
    }

    private suspend fun getMyPartnerInvitation(){
        val response = withContext(Dispatchers.IO) {
            noPartnerRepository.getMyPartnerInvitation(GetMyPartnerInvitationRequest())
        }
        when {
            response.isSuccessful -> {
                val body = response.body()!!
                invitation.value = body

                Log.d("TEST@","body.size :: ${body.size}")
                Log.d("TEST@","body[0].StartDate :: ${body[0].StartDate}")
                Log.d("TEST@","body[0].Salary :: ${body[0].Salary}")
                Log.d("TEST@","body[0].ParkingLots.Name :: ${body[0].ParkingLots.Name}")

                _currentStatus.value = when {
                    body.size > 0 -> {
                        Status.NOPART_INVITED
                    }
                    else -> Status.NOPART_NO_PARTNER
                }

                if(body.size>0){
                    position.value = 0
                    setInvitation()
                    isVisible.value = true
                }
            }
            response.code() == 403 -> {
                _currentStatus.value = Status.ERROR_EXPIRED
            }
            else -> {
                _currentStatus.value = Status.ERROR
                Log.d("TEST@","error.... response:: ${response.message()}")
            }
        }
    }

    fun setInvitation(){
        if(invitation.value!!.isNotEmpty()){
            invitation_name.value = invitation.value!![position.value!!].ParkingLots.Name
            start_day.value = invitation.value!![position.value!!].StartDate
            salary.value = invitation.value!![position.value!!].Salary
        }
    }

    fun rejectInvitation(){
        Log.d("TEST@","position :: ${position.value}")
        Log.d("TEST@","size :: ${invitation.value!!.size}")

        if(invitation.value!!.size>position.value!!+1){

            position.value = position.value!! + 1
            setInvitation()
        }else{
            position.value = position.value!! + 1
            isVisible.value = false
        }

    }

    fun acceptInvitation(){
        _currentStatus.value = Status.LOADING
        val invitation = position.value?.let { invitation.value?.get(it) }
        viewModelScope.launch(coroutineExceptionHandler) {
            val response = withContext(Dispatchers.IO) {
                noPartnerRepository.applyMyPartner(ApplyMyPartnerRequest(
                    PartnerBN = invitation!!.PartnerBN,
                    ParkingLN = invitation.ParkingLN
                ))
            }
            when {
                response.isSuccessful -> {
                    val body = response.body()!!

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