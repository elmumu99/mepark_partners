package com.mrpark1.meparkpartner.ui.newuser

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrpark1.meparkpartner.data.model.user.AddUserRequest
import com.mrpark1.meparkpartner.data.repository.implementations.NewUserRepositoryImpl
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
class NewUserViewModel @Inject constructor(private val newUserRepository: NewUserRepositoryImpl) :
    ViewModel() {

    lateinit var idToken: String

    val name = MutableLiveData("")
    val birth = MutableLiveData("")
    val contact = MutableLiveData("")

    private val _currentStatus = MutableLiveData<Status>()
    val currentStatus: LiveData<Status> = _currentStatus

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, e ->
        e.printStackTrace()
        when (e) {
            is UnknownHostException -> _currentStatus.value = Status.ERROR_INTERNET
            is JsonDataException -> _currentStatus.value = Status.ERROR
        }
    }

    //유저 정보 입력
    fun addNewUser() {
        if (currentStatus.value == Status.LOADING) return
        _currentStatus.value = Status.LOADING

        viewModelScope.launch(coroutineExceptionHandler) {
            val response = withContext(Dispatchers.IO) {
                newUserRepository.addUser(
                    AddUserRequest(
                        IDT = idToken,
                        Name = name.value!!,
                        Birth = birth.value!!,
                        Contact = contact.value!!
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
}