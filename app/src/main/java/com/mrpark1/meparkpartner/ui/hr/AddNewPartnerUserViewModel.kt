package com.mrpark1.meparkpartner.ui.hr

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrpark1.meparkpartner.data.model.common.PartnerUser
import com.mrpark1.meparkpartner.data.model.partner.AddNewEmployeeRequest
import com.mrpark1.meparkpartner.data.model.partner.GetMyPartnerUserRequest
import com.mrpark1.meparkpartner.data.model.partner.UpdateMyEmployeeRequest
import com.mrpark1.meparkpartner.data.repository.implementations.HrRepositoryImpl
import com.mrpark1.meparkpartner.ui.Status
import com.mrpark1.meparkpartner.util.Constants
import com.squareup.moshi.JsonDataException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddNewPartnerUserViewModel @Inject constructor(
    private val hrRepository: HrRepositoryImpl
) : ViewModel() {

    private val _currentStatus = MutableLiveData<Status>()
    val currentStatus: LiveData<Status> = _currentStatus

    private val cal = Calendar.getInstance()
    private val sdf = SimpleDateFormat("yyyy년 MM월 dd일")
    private val sdfAPI = SimpleDateFormat("yyyy-MM-dd")
    val year_start = MutableLiveData<Int>(cal.get(Calendar.YEAR))
    val month_start = MutableLiveData<Int>(cal.get(Calendar.MONTH))
    val day_start = MutableLiveData<Int>(cal.get(Calendar.DAY_OF_MONTH))
    val dateString = MutableLiveData(sdf.format(cal.timeInMillis))

    val userRole = MutableLiveData("직원")
    val userInfo = MutableLiveData<PartnerUser>()
    val ParkingLN = MutableLiveData("")
    val email = MutableLiveData("")
    val salary = MutableLiveData("")

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, e ->
        e.printStackTrace()
        when (e) {
            is UnknownHostException -> {
                _currentStatus.value = Status.ERROR_INTERNET
                Log.d("TEST@","updateUser UnknownHostException")
            }
            is JsonDataException -> {
                _currentStatus.value = Status.ERROR
                Log.d("TEST@","updateUser JsonDataException")
            }
            else ->{
                Log.d("TEST@","updateUser else :: ${e.message}")
            }
        }
    }


    fun setStartDay(year:Int, month:Int, day:Int){
        cal.set(year,month,day)
        year_start.value = year
        month_start.value = month
        day_start.value = day
        dateString.value = sdf.format(cal.timeInMillis)
    }

    fun setUserRole(role: String){
        userRole.value = role
    }

    fun setUserInfo(info: PartnerUser){
        userInfo.value = info
    }

    fun setMode(position: Int){
        if(position==-1){
            _currentStatus.value = Status.ADD_PARTNER_USER
        }else{
            _currentStatus.value = Status.UPDATE_PARTNER_USER
        }
    }

    fun addNewEmployee(){
        if (currentStatus.value == Status.LOADING) return
        _currentStatus.value = Status.LOADING

        viewModelScope.launch(coroutineExceptionHandler) {

            val response = withContext(Dispatchers.IO) {
                hrRepository.addNewEmployee(AddNewEmployeeRequest(
                    PartnerBN = Constants.PartnerBN,
                    Email = email.value!!,
                    ParkingLN = ParkingLN.value!!,
                    StartDate = sdfAPI.format(cal.timeInMillis),
                    Salary = salary.value!!
                ))
            }
            Log.d("TEST@","PartnerBN = ${Constants.PartnerBN}")
            Log.d("TEST@","Email = ${email.value}")
            Log.d("TEST@","ParkingLN = ${ParkingLN.value}")
            Log.d("TEST@","StartDate = ${sdfAPI.format(cal.timeInMillis)}")
            Log.d("TEST@","Salary = ${salary.value}")
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

    fun updateMyEmployee(){
        if (currentStatus.value == Status.LOADING) return
        _currentStatus.value = Status.LOADING

        var role = ""
        Log.d("TEST@","point :: 1")
        if(userRole.value!! == "관리자"){
            Log.d("TEST@","point :: 2")
            role = "Administrator"
        }else{
            Log.d("TEST@","point :: 3")
            role = "Employee"
        }

        viewModelScope.launch(coroutineExceptionHandler) {

            Log.d("TEST@","point :: 4")
            val response = withContext(Dispatchers.IO) {
                hrRepository.updateMyEmployee(
                    UpdateMyEmployeeRequest(
                    StartDate = sdfAPI.format(cal.timeInMillis),
                    Salary = salary.value!!,
                    UserID = userInfo.value!!.UserID,
                    Role = role)
                )
            }
            Log.d("TEST@","StartDate = ${sdfAPI.format(cal.timeInMillis)}")
            Log.d("TEST@","Salary = ${salary.value}")
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

    fun setParkingLot(name: String){
        for(i in Constants.parkingLots){
            if(name == i.Name){
                ParkingLN.value = i.ParkingLN
            }
        }
    }

    fun setEmail(emailString: String){
        email.value = emailString
    }

    fun setSalary(salaryString: String){
        salary.value = salaryString
    }
}