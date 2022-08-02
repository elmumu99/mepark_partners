package com.mrpark1.meparkpartner.ui.charge

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrpark1.meparkpartner.data.model.partner.GetMyPartnerInfoRequest
import com.mrpark1.meparkpartner.data.model.payment.*
import com.mrpark1.meparkpartner.data.repository.implementations.ChargeRepositoryImpl
import com.mrpark1.meparkpartner.ui.Status
import com.mrpark1.meparkpartner.util.Constants
import com.squareup.moshi.JsonDataException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChargeViewModel @Inject constructor(
    private val chargeRepository: ChargeRepositoryImpl
) : ViewModel() {

    private val _chargingHistory = MutableLiveData<ArrayList<CardPaymentInfo>>()
    val chargingHistory: LiveData<ArrayList<CardPaymentInfo>> = _chargingHistory

    private val chargingHistoryData = arrayListOf<CardPaymentInfo>()

    private val _pointHistory = MutableLiveData<ArrayList<PointReceipt>>()
    val pointHistory: LiveData<ArrayList<PointReceipt>> = _pointHistory

    private val pointHistoryData = arrayListOf<PointReceipt>()


    private val _currentStatus = MutableLiveData<Status>()
    val currentStatus: LiveData<Status> = _currentStatus

    val currentPosition = MutableLiveData(0)



    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, e ->
        e.printStackTrace()
        when (e) {
            is UnknownHostException -> _currentStatus.value = Status.ERROR_INTERNET
            is JsonDataException -> _currentStatus.value = Status.ERROR
        }
    }

    val point = MutableLiveData(0)

    val TotalUsageAmount = MutableLiveData(0)
    val VBANKCount = MutableLiveData(0)
    val CARDCount = MutableLiveData(0)

    val TotalCount = MutableLiveData(0)
    val ACount = MutableLiveData(0)
    val BCount = MutableLiveData(0)
    val NCount = MutableLiveData(0)

    val cal = Calendar.getInstance()
    val sdfCH = SimpleDateFormat("yyyyMMdd")
    val sdfPH = SimpleDateFormat("yyyy-MM-dd")

    val year_start = MutableLiveData<Int>(cal.get(Calendar.YEAR))
    val month_start = MutableLiveData<Int>(1)
    val day_start = MutableLiveData<Int>(1)
    val start_date = MutableLiveData<String>(
        "${cal.get(Calendar.YEAR)}년" +
                "${1}월" +
                "${1}일")


    val year_end = MutableLiveData<Int>(cal.get(Calendar.YEAR))
    val month_end = MutableLiveData<Int>(cal.get(Calendar.MONTH)+1)
    val day_end = MutableLiveData<Int>(cal.get(Calendar.DAY_OF_MONTH))
    val end_date = MutableLiveData<String>(
        "${cal.get(Calendar.YEAR)}년" +
                "${(cal.get(Calendar.MONTH) + 1)}월" +
                "${cal.get(Calendar.DAY_OF_MONTH)}일")

    val rent_num = MutableLiveData<Int>(0)
    val common_num = MutableLiveData<Int>(0)
    val un_apply_num = MutableLiveData<Int>(0)
    val all_num = MutableLiveData<Int>()


    private var startDateCH = cal.get(Calendar.YEAR).toString()+"0101"+"000000" //chargingHistory에서 사용하는 변수
    private var endDateCH = sdfCH.format(cal.timeInMillis)+"235959"

    private var startDatePH = cal.get(Calendar.YEAR).toString()+"-01-01"+" 00:00" //pointHistory에서 사용하는 변수
    private var endDatePH = sdfPH.format(cal.timeInMillis)+" 23:59"

    fun setStartDate(year: Int, month: Int, day: Int){
        this.year_start.value = year
        this.month_start.value = month
        this.day_start.value = day
        start_date.value = ""+year+"년"+month+"월"+day+"일"
        Log.d("TEST@","startDate :: ${start_date.value}")
    }

    fun setEndDate(year: Int, month: Int, day: Int){
        this.year_end.value = year
        this.month_end.value = month
        this.day_end.value = day
        end_date.value = ""+year+"년"+month+"월"+day+"일"
    }

    fun setDate(sYear: Int, sMonth: Int, sDay: Int,
                eYear: Int, eMonth: Int, eDay: Int){
        setStartDate(sYear, sMonth, sDay)
        setEndDate(eYear, eMonth, eDay)

        cal.set(sYear,sMonth-1,sDay)
        val startLong = cal.timeInMillis
        val startStringCH = sdfCH.format(startLong)
        startDateCH = startStringCH + "000000"

        cal.set(eYear, eMonth-1, eDay)
        cal.add(Calendar.DAY_OF_MONTH,2)
        val endLong = cal.timeInMillis
        val endStringCH = sdfCH.format(endLong)
        endDateCH = endStringCH + "235959"
        val endStringPH = sdfPH.format(endLong)
        endDatePH = endStringPH + " 23:59"



        when(currentPosition.value){
            0 ->{getChargingHistory()}
            1 ->{getPointHistory()}
        }
    }

    fun getChargingHistory() {
        if (currentStatus.value == Status.LOADING) return
        _currentStatus.value = Status.LOADING

        Log.d("TEST@","startDateCH :: $startDateCH")
        Log.d("TEST@","endDateCH :: $endDateCH")

        viewModelScope.launch(coroutineExceptionHandler) {
            val response = withContext(Dispatchers.IO) {
                chargeRepository.getChargingHistory(GetChargingHistoryRequest(
                    StartDate = startDateCH,
                    EndDate = endDateCH))
            }
            when {
                response.isSuccessful -> {
                    Log.d("TEST@", "response.body() :: ${response.body().toString()}")
                    _currentStatus.value = Status.SUCCESS
                    chargingHistoryData.clear()
                    chargingHistoryData.addAll(response.body()!!.Result)
                    setChargingHistory()
                }
                response.code() == 403 -> {
                    _currentStatus.value = Status.ERROR_EXPIRED
                    Log.d("TEST@", "403...response.body() :: ${response.message()}")
                }
                else -> {
                    _currentStatus.value = Status.ERROR
                    Log.d("TEST@", "else...response.body() :: ${response.message()}")
                }
            }
        }
    }

    fun setChargingHistory(){
        _chargingHistory.value = chargingHistoryData
    }


    fun getPointHistory() {
        if (currentStatus.value == Status.LOADING) return
        _currentStatus.value = Status.LOADING

        Log.d("TEST@","startDatePH :: $startDatePH")
        Log.d("TEST@","endDatePH :: $endDatePH")

        viewModelScope.launch(coroutineExceptionHandler) {
            val response = withContext(Dispatchers.IO) {
                chargeRepository.getPointHistory(GetPointHistoryRequest(
                    StartDate = startDatePH,
                    EndDate = endDatePH)
                )
            }
            when {
                response.isSuccessful -> {
                    Log.d("TEST@", "response.body() :: ${response.body().toString()}")
                    _currentStatus.value = Status.SUCCESS

                    TotalCount.value = response.body()?.TotalCount
                    ACount.value = response.body()?.ACount
                    BCount.value = response.body()?.BCount
                    NCount.value = response.body()?.NCount

                    TotalUsageAmount.value = response.body()!!.ATotalAmount + response.body()!!.BTotalAmount

                    pointHistoryData.clear()
                    pointHistoryData.addAll(response.body()!!.Result)
                    setPointHistory()
                }
                response.code() == 403 -> {
                    _currentStatus.value = Status.ERROR_EXPIRED
                    Log.d("TEST@", "403...response.body() :: ${response.message()}")
                }
                else -> {
                    _currentStatus.value = Status.ERROR
                    Log.d("TEST@", "else...response.body() :: ${response.message()}")
                }
            }
        }
    }

    fun setPointHistory(){
        _pointHistory.value = pointHistoryData
    }

    fun initData(){
        if (currentStatus.value == Status.LOADING) return
        _currentStatus.value = Status.LOADING

        viewModelScope.launch(coroutineExceptionHandler) {
            val response = withContext(Dispatchers.IO) {
                chargeRepository.getChargingHistory(GetChargingHistoryRequest(
                    StartDate = startDateCH,
                    EndDate = endDateCH))
            }
            when {
                response.isSuccessful -> {
                    Log.d("TEST@", "response.body() :: ${response.body().toString()}")
                    chargingHistoryData.clear()
                    chargingHistoryData.addAll(response.body()!!.Result)
                    setChargingHistory()
                }
                response.code() == 403 -> {
                    _currentStatus.value = Status.ERROR_EXPIRED
                    Log.d("TEST@", "403...response.body() :: ${response.message()}")
                }
                else -> {
                    _currentStatus.value = Status.ERROR
                    Log.d("TEST@", "else...response.body() :: ${response.message()}")
                }
            }
            var response2 : Response<GetPointHistoryResponse>
            try{
                response2 = withContext(Dispatchers.IO) {
                    chargeRepository.getPointHistory(GetPointHistoryRequest(
                        StartDate = startDatePH,
                        EndDate = endDatePH)
                    )
                }

            }catch (e: IOException){
                response2 = withContext(Dispatchers.IO) {
                    chargeRepository.getPointHistory(GetPointHistoryRequest(
                        StartDate = startDatePH,
                        EndDate = endDatePH)
                    )
                }
            }
            when {
                response2.isSuccessful -> {
                    Log.d("TEST@", "response.body() :: ${response2.body().toString()}")

                    TotalCount.value = response2.body()?.TotalCount
                    ACount.value = response2.body()?.ACount
                    BCount.value = response2.body()?.BCount
                    NCount.value = response2.body()?.NCount
                    point.value = response2.body()!!.Point

                    TotalUsageAmount.value = response2.body()!!.ATotalAmount + response2.body()!!.BTotalAmount
                    pointHistoryData.clear()
                    pointHistoryData.addAll(response2.body()!!.Result)
                    setPointHistory()
                }
                response2.code() == 403 -> {
                    _currentStatus.value = Status.ERROR_EXPIRED
                    Log.d("TEST@", "403...response.body() :: ${response2.message()}")
                }
                else -> {
                    _currentStatus.value = Status.ERROR
                    Log.d("TEST@", "else...response.body() :: ${response2.message()}")
                }
            }

            _currentStatus.value = Status.SUCCESS
        }
    }

    fun setCurrentPosition(position: Int){
        currentPosition.value = position

        when(position){
            0 ->{getChargingHistory()}
            1 ->{getPointHistory()}
        }
    }

    fun getMyPartnerInfo() {
        //요청 시에는 상태를 LOADING으로 전환, 이미 LOADING인 경우에는 중복 요청 방지를 위해 무시
        if (currentStatus.value == Status.LOADING) return
        _currentStatus.value = Status.LOADING

        //코루틴에서 진행 (에러 Handler를 달아줌)
        viewModelScope.launch(coroutineExceptionHandler) {

        }
    }

}