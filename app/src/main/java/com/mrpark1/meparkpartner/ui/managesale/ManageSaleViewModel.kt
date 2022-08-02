package com.mrpark1.meparkpartner.ui.managesale

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.mrpark1.meparkpartner.data.model.common.PartnerUser
import com.mrpark1.meparkpartner.data.model.managesale.GetMyParkingLotStatisticsRequest
import com.mrpark1.meparkpartner.data.model.managesale.GetMyPartnerStatisticsRequest
import com.mrpark1.meparkpartner.data.model.managesale.ParkingLotData
import com.mrpark1.meparkpartner.data.model.managesale.VisitPlaceData
import com.mrpark1.meparkpartner.data.model.parkinglot.GetMyParkingLotsRequest
import com.mrpark1.meparkpartner.data.repository.ManageSaleRepository
import com.mrpark1.meparkpartner.data.repository.implementations.ManageSaleRepositoryImpl
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
import kotlin.collections.ArrayList

@HiltViewModel
class ManageSaleViewModel @Inject constructor(
    private val manageSaleRepository: ManageSaleRepositoryImpl
) : ViewModel() {

    //현재 상태 저장
    private val _currentStatus = MutableLiveData<Status>()
    val currentStatus: LiveData<Status> = _currentStatus

    private val mode = MutableLiveData(0) //0: 주차장, 1: 파트너

    //코루틴 내에서 에러 발생 시 캐치 (인터넷 오류 등 분기처리)
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, e ->
        e.printStackTrace()
        Log.d("TEST@","manageSale :: $e")
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



    private val cal = Calendar.getInstance()
    private val sdf = SimpleDateFormat("yyyy-MM-dd")

    private val _startDate = MutableLiveData(sdf.format(cal.timeInMillis))
    val startDate: LiveData<String> = _startDate

    private val _endDate = MutableLiveData(sdf.format(cal.timeInMillis))
    val endDate: LiveData<String> = _endDate

    val titleString = MutableLiveData("")

    val visitPlaceList = MutableLiveData<ArrayList<VisitPlaceData>>()
    private val visitPlaceListData = arrayListOf<VisitPlaceData>()

    val parkingLotList = MutableLiveData<ArrayList<ParkingLotData>>()
    private val parkingLotListData = arrayListOf<ParkingLotData>()

    val totalSales = MutableLiveData("0")

    val barGraphTitle = MutableLiveData("방문지별 매출 그래프")
    val barGraphData = MutableLiveData<BarData>()
    val barGraphLabel = MutableLiveData<ArrayList<String>>()
    val _VisitPlaceName = arrayListOf<String>()
    val _ParkingLotName = arrayListOf<String>()
    private val _barGraphData = arrayListOf<BarEntry>()

    val pieGraphData = MutableLiveData<PieData>()


    fun setSelectedDate(startDate: String,endDate: String) {
        if(_startDate.value==startDate&&_endDate.value==endDate){

        }else{
            _startDate.value = startDate
            _endDate.value = endDate

            setMode(mode.value!!)
        }
    }

    fun setMode(mode: Int){
        this.mode.value = mode


        when(mode){  //0: 주차장, 1: 파트너
            0 -> {

                getMyParkingLotStatistics()
            }
            1 -> {
                getMyPartnerStatistics()
            }
        }
    }

    fun setTitle(title: String){
        titleString.value = title
    }

    fun setMode(mode: Status){
        _currentStatus.value = mode
    }

    fun getMyPartnerStatistics() {
        //요청 시에는 상태를 LOADING으로 전환, 이미 LOADING인 경우에는 중복 요청 방지를 위해 무시
        if (currentStatus.value == Status.LOADING) return
        _currentStatus.value = Status.LOADING

        Log.d("TEST@","Partner StartDate :: ${startDate.value!!}")
        Log.d("TEST@","Partner EndDate :: ${endDate.value!!}")

        //코루틴에서 진행 (에러 Handler를 달아줌)
        viewModelScope.launch(coroutineExceptionHandler) {
            val response = withContext(Dispatchers.IO) { //네트워크 작업은 IO 쓰레드 사용
                manageSaleRepository.getMyPartnerStatistics(GetMyPartnerStatisticsRequest(
                    StartDate = startDate.value!!,
                    EndDate = endDate.value!!
                ))
            }
            when {
                //요청 성공 시 데이터 갱신
                response.isSuccessful -> {
                    _currentStatus.value = Status.SUCCESS
                    parkingLotListData.clear()
                    parkingLotListData.addAll(response.body()!!.ParkingLots)
                    setParkingLotList()
                    setSales(response.body()!!.TotalProfit.toString())
                    barGraphTitle.value = "주차장별 매출 그래프"

                    _barGraphData.clear()
                    _ParkingLotName.clear()
                    for((position, data) in parkingLotListData.withIndex()){
//                        val entry = BarEntry(position.toFloat(),position.toFloat())
                        val entry = BarEntry(position.toFloat(),data.ParkingLotProfit.toFloat())
                        _barGraphData.add(entry)
                        Log.d("TEST@","parkingLotName :: P")
                        _ParkingLotName.add(data.Name)
                    }
                    barGraphLabel.value = _ParkingLotName
                    val barDataSet = BarDataSet(_barGraphData,"barChartName")
                    barDataSet.axisDependency = YAxis.AxisDependency.RIGHT
                    barDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
                    barGraphData.value = BarData(barDataSet)

//                    val Payment1: Int, //현금 결제
//                    val Payment2: Int, // 간편 결제
//                    val Payment0: Int, // 미 결제

                    //pieChart
                    val noOfEmp = arrayListOf<PieEntry>()
                    noOfEmp.add(PieEntry(response.body()!!.Payment1.toFloat(),"현금결제"))
                    noOfEmp.add(PieEntry(response.body()!!.Payment2.toFloat(),"간편결제"))
                    noOfEmp.add(PieEntry(response.body()!!.Payment0.toFloat(),"미결제"))
                    val dataset = PieDataSet(noOfEmp,"현금/간편결제 비율")
                    dataset.colors = ColorTemplate.COLORFUL_COLORS.toList()
                    pieGraphData.value = PieData(dataset)




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
    fun getMyParkingLotStatistics() {
        //요청 시에는 상태를 LOADING으로 전환, 이미 LOADING인 경우에는 중복 요청 방지를 위해 무시
        if (currentStatus.value == Status.LOADING) return
        _currentStatus.value = Status.LOADING

        //코루틴에서 진행 (에러 Handler를 달아줌)

        Log.d("TEST@","parkingLot ParkingLN :: ${Constants.selectedParkingLot.ParkingLN}")
        Log.d("TEST@","parkingLot StartDate :: ${startDate.value!!}")
        Log.d("TEST@","parkingLot EndDate :: ${endDate.value!!}")
        viewModelScope.launch(coroutineExceptionHandler) {
            val response = withContext(Dispatchers.IO) { //네트워크 작업은 IO 쓰레드 사용
                manageSaleRepository.getMyParkingLotStatistics(
                    GetMyParkingLotStatisticsRequest(
                        ParkingLN = Constants.selectedParkingLot.ParkingLN,
                        StartDate = startDate.value!!,
                        EndDate = endDate.value!!,
                    )
                )
            }
            when {
                //요청 성공 시 데이터 갱신
                response.isSuccessful -> {
                    visitPlaceListData.clear()
                    visitPlaceListData.addAll(response.body()!!.VisitPlaces)
                    setVisitPlaceList()
                    _currentStatus.value = Status.SUCCESS
                    setSales(response.body()!!.TotalProfit.toString())


                    //barChart
                    barGraphTitle.value = "방문지별 매출 그래프"

                    _barGraphData.clear()
                    _VisitPlaceName.clear()
                    for((position, data) in visitPlaceListData.withIndex()){
//                        val entry = BarEntry(position.toFloat(),position.toFloat())
                        val entry = BarEntry(position.toFloat(),data.VisitPlaceProfit.toFloat())
                        _barGraphData.add(entry)
                        _VisitPlaceName.add(data.VisitPlace)
                        Log.d("TEST@","barGraphDataName :: ${data.VisitPlace}")
                    }
                    barGraphLabel.value = _VisitPlaceName
                    val barDataSet = BarDataSet(_barGraphData,"")
                    barDataSet.axisDependency = YAxis.AxisDependency.RIGHT
                    barDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
                    barGraphData.value = BarData(barDataSet)

//                    val Payment1: Int, //현금 결제
//                    val Payment2: Int, // 간편 결제
//                    val Payment0: Int, // 미 결제

                    //pieChart
                    val noOfEmp = arrayListOf<PieEntry>()
                    noOfEmp.add(PieEntry(response.body()!!.Payment1.toFloat(),"현금결제"))
                    noOfEmp.add(PieEntry(response.body()!!.Payment2.toFloat(),"간편결제"))
                    noOfEmp.add(PieEntry(response.body()!!.Payment0.toFloat(),"미결제"))
//                    noOfEmp.add(PieEntry(1f,"현금결제"))
//                    noOfEmp.add(PieEntry(2f,"간편결제"))
//                    noOfEmp.add(PieEntry(3f,"미결제"))
                    val dataset = PieDataSet(noOfEmp,"")
                    dataset.apply {
                        colors = ColorTemplate.COLORFUL_COLORS.toList()
                        valueTextColor = Color.BLACK
                        valueTextSize = 11f

                    }
                    pieGraphData.value = PieData(dataset)



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
    private fun setParkingLotList(){
        parkingLotList.value = parkingLotListData
    }
    private fun setVisitPlaceList(){
        visitPlaceList.value = visitPlaceListData
    }
    fun setSales(sales: String){
        totalSales.value = sales
    }
}