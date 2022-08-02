package com.mrpark1.meparkpartner.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrpark1.meparkpartner.data.model.common.ParkingLot
import com.mrpark1.meparkpartner.data.model.parkinglot.GetMyParkingLotsRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.car.GetParkedCarsRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.commute.UpdateCommutingRequest
import com.mrpark1.meparkpartner.data.model.partner.GetMyPartnerInfoRequest
import com.mrpark1.meparkpartner.data.repository.implementations.MainRepositoryImpl
import com.mrpark1.meparkpartner.ui.Status
import com.mrpark1.meparkpartner.util.Constants
import com.mrpark1.meparkpartner.util.SharedPrefUtil
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

//ViewModel도 마찬가지로 @HiltViewModel 어노테이션을 달아줘야 DI 가능
@HiltViewModel
class MainViewModel @Inject constructor(
    //Repository 구현체를 주입받음
    private val mainRepository: MainRepositoryImpl,
    private val sharedPrefUtil: SharedPrefUtil
) : ViewModel() {

    //로컬에 저장된 사용자 이름 가져오기
    val userName = sharedPrefUtil.getString(SharedPrefUtil.KEY_USER_NAME, "")

    //주차장 목록
    private val _parkingLots = ArrayList<ParkingLot>()
    val parkingLots: List<ParkingLot> = _parkingLots

    //현재 상태 저장
    private val _currentStatus = MutableLiveData<Status>()
    val currentStatus: LiveData<Status> = _currentStatus

    //현재 선택한 주차장
    private val _selectedParkingLot = MutableLiveData<ParkingLot>()
    val selectedParkingLot: LiveData<ParkingLot> = _selectedParkingLot

    //주차 차량 댓수
    private val _parkedCars = MutableLiveData(0)
    val parkedCars: LiveData<Int> = _parkedCars

    val commutingStatus = MutableLiveData(Constants.CommutingStatus)

    //서버 시간
    private val _serverTime = MutableLiveData("")
    val serverTime: LiveData<String> = _serverTime

    //코루틴 내에서 에러 발생 시 캐치 (인터넷 오류 등 분기처리)
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, e ->
        e.printStackTrace()
        when (e) {
            is UnknownHostException -> _currentStatus.value = Status.ERROR_INTERNET
            is JsonDataException -> _currentStatus.value = Status.ERROR
        }
    }

    //주차장 목록 불러오기
    fun getMyParkingLots() {
        //요청 시에는 상태를 LOADING으로 전환, 이미 LOADING인 경우에는 중복 요청 방지를 위해 무시
        if (currentStatus.value == Status.LOADING) return
        _currentStatus.value = Status.LOADING

        //코루틴에서 진행 (에러 Handler를 달아줌)
        viewModelScope.launch(coroutineExceptionHandler) {
            val response = withContext(Dispatchers.IO) { //네트워크 작업은 IO 쓰레드 사용
                mainRepository.getMyParkingLots(GetMyParkingLotsRequest())
            }
            when {
                //요청 성공 시 데이터 갱신
                response.isSuccessful -> {
                    _parkingLots.clear()
                    _parkingLots.addAll(response.body()!!)
                    Constants.parkingLots.clear()
                    Constants.parkingLots.addAll(response.body()!!)
                    Log.d("TEST@","getMyParkingLots :: ${response.body().toString()}")
                    if (parkingLots.isEmpty()) {
                        _currentStatus.value = Status.MAIN_NO_PARKING_LOTS
                        return@launch
                    }else{
                        try{
                            //selectedParkingLot이 초기화 된 경우
                            setCurrentParkingLot(Constants.selectedParkingLot)
                        }catch (e: Exception){
                            //selectedParkingLot이 초기화 안된 경우
                            setCurrentParkingLot(parkingLots[0])
                        }

                    }

//                    val lastParkingLN =
//                        sharedPrefUtil.getString(SharedPrefUtil.KEY_SELECTED_PARKING_LOT, "")
//
//                    Log.d("TEST@","lastParkingLN :: $lastParkingLN")
//                    Log.d("TEST@","parkingLots.size :: ${parkingLots.size}")
//                    if (lastParkingLN.isNullOrEmpty()) setCurrentParkingLot(parkingLots[0])
//                    else {
//                        for (parkingLot in parkingLots) {
//                            if (parkingLot.ParkingLN == lastParkingLN)
//                                setCurrentParkingLot(parkingLot)
//                        }
//
//                    }
                }
                //세션 만료, 스플래시 화면으로
                response.code() == 403 -> {
                    _currentStatus.value = Status.ERROR_EXPIRED
                }
                //기타 에러 (자유롭게 예외처리 가능)
                else -> {
                    _currentStatus.value = Status.ERROR
                }
            }
        }
    }

    //선택한 주차장 로컬에 저장
    fun setCurrentParkingLot(parkingLot: ParkingLot) {
        Log.d("TEST@","setCurrentParkingLot :: ")
        Constants.selectedParkingLot = parkingLot

        _selectedParkingLot.value = parkingLot
        sharedPrefUtil.put(SharedPrefUtil.KEY_SELECTED_PARKING_LOT, parkingLot.ParkingLN)
        getParkedCars(parkingLot.ParkingLN)
    }

    //입차 차량 목록 불러오기
    private fun getParkedCars(parkingLN: String) {
        _currentStatus.value = Status.LOADING

        viewModelScope.launch(coroutineExceptionHandler) {
            val response = withContext(Dispatchers.IO) {
                mainRepository.getParkedCars(GetParkedCarsRequest(parkingLN))
            }
            when {
                response.isSuccessful -> {
                    _parkedCars.value = response.body()!!.Cars.size

                    val time = response.body()!!.NowDateTime
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)
                    val dateFormatShow = SimpleDateFormat("yyyy년 MM월 dd일 \naa hh:mm", Locale.KOREA)
                    _serverTime.value = dateFormatShow.format(dateFormat.parse(time)!!)

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

    //QR 출퇴근 요청
    fun updateCommuting(parkingLN: String) {
        if (currentStatus.value == Status.LOADING) return
        _currentStatus.value = Status.LOADING

        viewModelScope.launch(coroutineExceptionHandler) {
            val response = withContext(Dispatchers.IO) {
                mainRepository.updateCommuting(UpdateCommutingRequest(parkingLN))
            }
            when {
                response.isSuccessful -> {
                    _currentStatus.value = Status.SUCCESS
                    when(Constants.CommutingStatus){// 출근 상태 1: 근무한적 없음 2: 근무중 3: 퇴근  0: 초기화 전
                        "1" ->{ Constants.CommutingStatus = "2" }
                        "2" ->{ Constants.CommutingStatus = "3" }
                        "3" ->{ Constants.CommutingStatus = "2" }
                    }
                    commutingStatus.value = Constants.CommutingStatus
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