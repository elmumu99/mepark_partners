package com.mrpark1.meparkpartner.ui.hr

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrpark1.meparkpartner.data.model.common.PartnerUser
import com.mrpark1.meparkpartner.data.model.common.PartnerUserCommutingHistory
import com.mrpark1.meparkpartner.data.model.partner.GetMyPartnerUserRequest
import com.mrpark1.meparkpartner.data.model.partner.GetMyPartnerUsersCommutingHistoryRequest
import com.mrpark1.meparkpartner.data.model.partner.RemoveMyEmployeeRequest
import com.mrpark1.meparkpartner.data.repository.implementations.HrRepositoryImpl
import com.mrpark1.meparkpartner.ui.Status
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
class HrViewModel @Inject constructor(
    private val hrRepository: HrRepositoryImpl
) : ViewModel() {

    private val _partnerUserList = MutableLiveData<ArrayList<PartnerUser>>()
    val partnerUserList: LiveData<ArrayList<PartnerUser>> = _partnerUserList

    private val partnerUserData = arrayListOf<PartnerUser>()

    val workCount = MutableLiveData(0)
    val leaveCount = MutableLiveData(0)


    private val _commutingHistoryList = ArrayList<PartnerUserCommutingHistory>()
    val commutingHistoryList: List<PartnerUserCommutingHistory> = _commutingHistoryList


    private val _currentStatus = MutableLiveData<Status>()
    val currentStatus: LiveData<Status> = _currentStatus

    val isEditMode = MutableLiveData(false)

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, e ->
        e.printStackTrace()
        when (e) {
            is UnknownHostException -> _currentStatus.value = Status.ERROR_INTERNET
            is JsonDataException -> _currentStatus.value = Status.ERROR
        }
    }

    val cal = Calendar.getInstance()
    val sdf = SimpleDateFormat("yyyy-MM-dd")

    val year = MutableLiveData<Int>(cal.get(Calendar.YEAR))
    val month = MutableLiveData<Int>(cal.get(Calendar.MONTH)+1)
    val day = MutableLiveData<Int>(cal.get(Calendar.DAY_OF_MONTH))
    val date = MutableLiveData<String>(
        "${cal.get(Calendar.YEAR)}년" +
                "${(cal.get(Calendar.MONTH) + 1)}월" +
                "${cal.get(Calendar.DAY_OF_MONTH)}일")

    fun setDate(year: Int, month: Int, day: Int){
        this.year.value = year
        this.month.value = month
        this.day.value = day
        date.value = ""+year+"년"+month+"월"+day+"일"
        Log.d("TEST@","startDate :: ${date.value}")
        cal.set(year,month-1,day)
    }


    fun getMyPartnerUsersCommutingHistory(){
        if (currentStatus.value == Status.LOADING) return
        _currentStatus.value = Status.LOADING

        val startLong = cal.timeInMillis
        val startDateString = sdf.format(startLong)

        cal.add(Calendar.DAY_OF_MONTH,1)
        val endLong = cal.timeInMillis
        val endDateString = sdf.format(endLong)

        cal.add(Calendar.DAY_OF_MONTH,-1) //calendar 원위치

        viewModelScope.launch(coroutineExceptionHandler) {
            val response = withContext(Dispatchers.IO) {
                hrRepository.getMyPartnerUsersCommutingHistory(
                    GetMyPartnerUsersCommutingHistoryRequest(startDateString,endDateString)
                )
            }
            when {
                response.isSuccessful -> {
                    _currentStatus.value = Status.SUCCESS
                    _commutingHistoryList.clear()
                    if(response.body()!=null&&response.body()!!.isNotEmpty()){
                        _commutingHistoryList.addAll(response.body()!!)
                    }
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


    fun getMyPartnerUsers(){
        if (currentStatus.value == Status.LOADING) return
        _currentStatus.value = Status.LOADING

        viewModelScope.launch(coroutineExceptionHandler) {
            val response = withContext(Dispatchers.IO) {
                hrRepository.getMyPartnerUsers(GetMyPartnerUserRequest())
            }
            Log.d("TEST@","response.body() ::......")
            when {
                response.isSuccessful -> {
                    Log.d("TEST@","response.body() :: ${response.body().toString()}")
                    Log.d("TEST@","response.body().size :: ${response.body()!!.size}")
                    _currentStatus.value = Status.SUCCESS
                    partnerUserData.clear()
                    partnerUserData.addAll(response.body()!!)
                    setPartnerUsers()
                }
                response.code() == 403 -> {
                    _currentStatus.value = Status.ERROR_EXPIRED
                    Log.d("TEST@","403...response.body() :: ${response.message()}")
                }
                else -> {
                    _currentStatus.value = Status.ERROR
                    Log.d("TEST@","else...response.body() :: ${response.message()}")
                }
            }
        }
    }

    fun setPartnerUsers(){
        _partnerUserList.value = partnerUserData
        workCount.value = 0
        leaveCount.value = 0

        for(i in partnerUserData){
            when(i.CommutingStatus){
                "1"->{ //근무한적없음
                }
                "2" ->{ //근무중
                    workCount.value = workCount.value!!+1
                }
                "3" ->{ //퇴근
                    leaveCount.value = leaveCount.value!!+1
                }
            }
        }
    }

    fun isWork(status: String){
        when(status){
            "1" ->{}
            "2" ->{}
            "3" ->{}
        }
    }

    fun changeEditMode(){
        isEditMode.value = !(isEditMode.value!!)
    }

    fun deletePartnerUser(position: Int){
        if (currentStatus.value == Status.LOADING) return
        _currentStatus.value = Status.LOADING

        viewModelScope.launch(coroutineExceptionHandler) {
            val response = withContext(Dispatchers.IO) {
                hrRepository.removeMyEmployee(RemoveMyEmployeeRequest(
                    UserID = partnerUserData[position].UserID)
                )
            }
            Log.d("TEST@","response.body() ::......")
            when {
                response.isSuccessful -> {
                    _currentStatus.value = Status.SUCCESS
                    partnerUserData.remove(partnerUserData[position])
                    setPartnerUsers()
                }
                response.code() == 403 -> {
                    _currentStatus.value = Status.ERROR_EXPIRED
                    Log.d("TEST@","403...response.body() :: ${response.message()}")
                }
                else -> {
                    _currentStatus.value = Status.ERROR
                    Log.d("TEST@","else...response.body() :: ${response.message()}")
                }
            }
        }
    }
}