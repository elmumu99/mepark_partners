package com.mrpark1.meparkpartner.ui.monthlyparking

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrpark1.meparkpartner.data.model.common.MonthParkedCar
import com.mrpark1.meparkpartner.data.model.parkinglot.car.AddMonthParkedCarRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.car.RemoveMonthParkedCarRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.car.UpdateMonthParkedCarRequest
import com.mrpark1.meparkpartner.data.model.partner.ApplyNewPartnerRequest
import com.mrpark1.meparkpartner.data.repository.implementations.MonthParkRepositoryImpl
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
class AddNewMonthlyParkingViewModel @Inject constructor(
    private val monthParkRepository: MonthParkRepositoryImpl
): ViewModel() {
    //현재 상태 저장
    private val _currentStatus = MutableLiveData<Status>()
    val currentStatus: LiveData<Status> = _currentStatus

    //코루틴 내에서 에러 발생 시 캐치 (인터넷 오류 등 분기처리)
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, e ->
        e.printStackTrace()
        Log.d("TEST@","MonthlyParkingViewModel :: $e")
        when (e) {
            is UnknownHostException -> _currentStatus.value = Status.ERROR_INTERNET
            is JsonDataException -> {
                _currentStatus.value = Status.ERROR
            }
            else -> {
                _currentStatus.value = Status.ERROR
            }
        }
    }

    val lp = MutableLiveData("")
    val carType = MutableLiveData("")
    val contact = MutableLiveData("")
    val memo = MutableLiveData("")
    val profit = MutableLiveData("")

    var monthCarID = ""

    val cal = Calendar.getInstance()
    val sdf = SimpleDateFormat("yyyy-MM-dd")

    val year_start = MutableLiveData(cal.get(Calendar.YEAR))
    val month_start = MutableLiveData(cal.get(Calendar.MONTH))
    val day_start = MutableLiveData(cal.get(Calendar.DAY_OF_MONTH))
    val start_date = MutableLiveData(sdf.format(cal.timeInMillis))


    val year_end = MutableLiveData(cal.get(Calendar.YEAR))
    val month_end = MutableLiveData(cal.get(Calendar.MONTH))
    val day_end = MutableLiveData(cal.get(Calendar.DAY_OF_MONTH))
    val end_date = MutableLiveData(sdf.format(cal.timeInMillis))


    fun setStartDate(year: Int, month: Int, day: Int){
        this.year_start.value = year
        this.month_start.value = month
        this.day_start.value = day
        cal.set(year,month,day)
        start_date.value = sdf.format(cal.time)
    }

    fun setEndDate(year: Int, month: Int, day: Int){
        this.year_end.value = year
        this.month_end.value = month
        this.day_end.value = day
        cal.set(year,month,day)
        end_date.value = sdf.format(cal.time)
    }

    fun setDate(sYear: Int, sMonth: Int, sDay: Int,
                eYear: Int, eMonth: Int, eDay: Int){
        setStartDate(sYear, sMonth, sDay)
        setEndDate(eYear, eMonth, eDay)

    }

    fun setLP(lp: String){
        this.lp.value = lp
    }

    fun setCarType(carType: String){
        this.carType.value = carType
    }

    fun setContact(contact: String){
        this.contact.value = contact
    }

    fun setMemo(memo: String){
        this.memo.value = memo
    }

    fun setProfit(profit: String){
        this.profit.value = profit
    }

    fun setUpdateMode(car: MonthParkedCar){
        lp.value = car.LP
        contact.value = car.Contact
        start_date.value = car.StartDate
        end_date.value = car.EndDate
        profit.value = car.Profit
        monthCarID = car.MonthCarID
        carType.value = car.CarType
    }

    //월주차 추가
    fun addNewMonthlyParkingCar() {
        if (currentStatus.value == Status.LOADING) return
        _currentStatus.value = Status.LOADING




        viewModelScope.launch(coroutineExceptionHandler) {
            val response = withContext(Dispatchers.IO) {
                monthParkRepository.addMonthParkedCar(
                    AddMonthParkedCarRequest(
                        ParkingLN = Constants.selectedParkingLot.ParkingLN,
                        LP = lp.value!!,
                        CarType = carType.value!!,
                        Contact = contact.value!!,
                        Memo = memo.value!!,
                        StartDate = start_date.value!!,
                        EndDate = end_date.value!!,
                        Payment = "1", //0 이면 미결제 , 1이면 현금결제 , 2이면 간편결제
                        Profit = profit.value!!
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

    //월주차 수정
    fun updateNewMonthlyParkingCar() {
        if (currentStatus.value == Status.LOADING) return
        _currentStatus.value = Status.LOADING




        viewModelScope.launch(coroutineExceptionHandler) {
            val response = withContext(Dispatchers.IO) {
                monthParkRepository.updateMonthParkedCar(
                    UpdateMonthParkedCarRequest(
                        ParkingLN = Constants.selectedParkingLot.ParkingLN,
                        MonthCarID = monthCarID,
                        LP = lp.value!!,
                        CarType = carType.value!!,
                        Contact = contact.value!!,
                        Memo = memo.value!!,
                        StartDate = start_date.value!!,
                        EndDate = end_date.value!!,
                        Payment = "1", //0 이면 미결제 , 1이면 현금결제 , 2이면 간편결제
                        Profit = profit.value!!
                    )
                )

            }


            when {
                response.isSuccessful -> {
                    _currentStatus.value = Status.MONTH_PARK_UPDATE_SUCCESS
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

    //월주차 삭제
    fun removeMonthlyParkingCar() {
        if (currentStatus.value == Status.LOADING) return
        _currentStatus.value = Status.LOADING


        viewModelScope.launch(coroutineExceptionHandler) {
            val response = withContext(Dispatchers.IO) {
                monthParkRepository.removeMonthParkedCar(
                    RemoveMonthParkedCarRequest(
                        ParkingLN = Constants.selectedParkingLot.ParkingLN,
                        MonthCarID = monthCarID
                    )
                )

            }


            when {
                response.isSuccessful -> {
                    _currentStatus.value = Status.MONTH_PARK_UPDATE_SUCCESS
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