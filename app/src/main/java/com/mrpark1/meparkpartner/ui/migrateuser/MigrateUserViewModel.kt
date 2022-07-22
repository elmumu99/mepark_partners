package com.mrpark1.meparkpartner.ui.migrateuser

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.data.model.user.GetUpdateEmailAuthCodeRequest
import com.mrpark1.meparkpartner.data.model.user.UpdateUserEmailRequest
import com.mrpark1.meparkpartner.data.repository.implementations.MigrateUserRepositoryImpl
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
class MigrateUserViewModel @Inject constructor(private val migrateUserRepository: MigrateUserRepositoryImpl) :
    ViewModel() {

    lateinit var idToken: String

    val email = MutableLiveData("")
    val authMethodRadio = MutableLiveData(R.id.rb_migrate_user_email)
    val authCode = MutableLiveData("")

    private val _currentStatus = MutableLiveData<Status>()
    val currentStatus: LiveData<Status> = _currentStatus

    private val _showAuthCodeField = MutableLiveData(false)
    val showAuthCodeField: LiveData<Boolean> = _showAuthCodeField

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, e ->
        e.printStackTrace()
        when (e) {
            is UnknownHostException -> _currentStatus.value = Status.ERROR_INTERNET
            is JsonDataException -> _currentStatus.value = Status.ERROR
        }
    }

    //인증번호 받기
    fun getUpdateEmailAuthCode() {
        _currentStatus.value = Status.LOADING
        viewModelScope.launch(coroutineExceptionHandler) {
            val response = withContext(Dispatchers.IO) {
                migrateUserRepository.getUpdateEmailAuthCode(
                    GetUpdateEmailAuthCodeRequest(
                        Email = email.value!!,
                        AuthByEmail = authMethodRadio.value!! == R.id.rb_migrate_user_email
                    )
                )
            }
            when {
                response.isSuccessful -> {
                    _currentStatus.value = Status.MIGRATE_AUTH_CODE_SENT
                    _showAuthCodeField.value = true
                }
                response.code() == 400 -> {
                    _currentStatus.value = Status.MIGRATE_ERROR_INVALID_EMAIL
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

    //계정 이메일 수정
    fun updateUserEmail() {
        _currentStatus.value = Status.LOADING
        viewModelScope.launch(coroutineExceptionHandler) {
            val response = withContext(Dispatchers.IO) {
                migrateUserRepository.updateUserEmail(
                    UpdateUserEmailRequest(
                        IDT = idToken,
                        Email = email.value!!,
                        AuthCode = authCode.value!!
                    )
                )
            }
            when {
                response.isSuccessful -> {
                    _currentStatus.value = Status.SUCCESS
                }
                else -> {
                    _currentStatus.value = Status.MIGRATE_ERROR_WRONG_AUTH_CODE
                }
            }
        }
    }
}