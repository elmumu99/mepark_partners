package com.mrpark1.meparkpartner.ui.managesale

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.tabs.TabLayout
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.databinding.ActivityManageSaleBinding
import com.mrpark1.meparkpartner.ui.Status
import com.mrpark1.meparkpartner.ui.common.BaseActivity
import com.mrpark1.meparkpartner.ui.dialogs.spinner.BottomSheetSpinner
import com.mrpark1.meparkpartner.util.CommandUtil
import com.mrpark1.meparkpartner.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageSaleActivity : BaseActivity<ActivityManageSaleBinding>(R.layout.activity_manage_sale) {

    private val viewModel: ManageSaleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setRecyclerView()

        initChart()

        binding.tbManageSale.setNavigationOnClickListener { finish() }
        //ViewModel 변경사항 감지 및 동작 수행
        viewModel.currentStatus.observe(this) {
            when (it) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> {}
                Status.ERROR_INTERNET -> snackBar(getString(R.string.common_error_internet))
                Status.ERROR_EXPIRED -> sessionExpired()
                Status.ERROR -> snackBar(getString(R.string.common_error_unknown))
                Status.MANAGE_SALE_PARKING_LOT ->{
                    binding.rvParkingLotLoc.visibility = View.VISIBLE
                    binding.rvPartnerLoc.visibility = View.GONE
                    binding.ivSelectParkinglot.visibility = View.VISIBLE
                    binding.chartBarGraph.clear()
                    binding.chartPieGraph.clear()
                    viewModel.setMode(0) //주차장
                }
                Status.MANAGE_SALE_PARTNER ->{
                    binding.rvParkingLotLoc.visibility = View.GONE
                    binding.rvPartnerLoc.visibility = View.VISIBLE
                    binding.ivSelectParkinglot.visibility = View.GONE
                    binding.chartBarGraph.clear()
                    binding.chartPieGraph.clear()
                    viewModel.setMode(1) //파트너
                }
                else -> {}
            }
            if (it != Status.LOADING) loadingDialog.cancel()
        }
        viewModel.setTitle(Constants.selectedParkingLot.Name)

        binding.tlManageSaleTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab?.position

                if(position==0){
                    viewModel.setTitle(Constants.selectedParkingLot.Name)
                    viewModel.setMode(Status.MANAGE_SALE_PARKING_LOT)
                }else{
                    viewModel.setTitle("파트너")
                    viewModel.setMode(Status.MANAGE_SALE_PARTNER)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.calendarLayout.setOnClickListener{
            DatePickerDialog(
                this,
                R.style.DatePickerDialogTheme,
                { _, sYear, sMonth, sDay ->
                    DatePickerDialog(
                        this,
                        R.style.DatePickerDialogTheme,
                        { _, eYear, eMonth, eDay ->
                            viewModel.setSelectedDate(
                                "$sYear-${String.format("%02d", sMonth + 1)}-" +
                                        String.format("%02d", sDay),
                                "$eYear-${String.format("%02d", eMonth + 1)}-" +
                                        String.format("%02d", eDay)
                            ) },
                        viewModel.endDate.value!!.substring(0, 4).toInt(),
                        viewModel.endDate.value!!.substring(5, 7).toInt() - 1,
                        viewModel.endDate.value!!.substring(8, 10).toInt()
                    ).show()
                },
                viewModel.startDate.value!!.substring(0, 4).toInt(),
                viewModel.startDate.value!!.substring(5, 7).toInt() - 1,
                viewModel.startDate.value!!.substring(8, 10).toInt()
            ).show()
        }

        binding.tvTabTitle.setOnClickListener {
            showParkingLotList()
        }

        binding.ivSelectParkinglot.setOnClickListener {
            showParkingLotList()
        }

        viewModel.startDate.observe(this) {
            binding.tvStartDay.text = it
        }
        viewModel.endDate.observe(this) {
            binding.tvEndDay.text = it
        }

        viewModel.titleString.observe(this){
            binding.tvTabTitle.text = it.toString()
        }

        viewModel.visitPlaceList.observe(this){
            (binding.rvParkingLotLoc.adapter as ParkingLotSaleAdapter).setListItem(it)
        }
        viewModel.parkingLotList.observe(this){
            (binding.rvPartnerLoc.adapter as PartnerSaleAdapter).setListItem(it)
        }
        viewModel.totalSales.observe(this){
            binding.tvSales.text = CommandUtil.addCommaNumInt(it)
        }

        viewModel.barGraphData.observe(this){
            //barGraph
            (binding.chartBarGraph).run {
                data = viewModel.barGraphData.value
                if(viewModel.barGraphLabel.value != null){
                    xAxis.valueFormatter = MyXAxisValueFormatter(viewModel.barGraphLabel.value!!)// 라벨 값 포멧 설정
                }
                animateXY(1000,1000)
                invalidate()
            }
        }

        viewModel.pieGraphData.observe(this){
            //pieGraph
            (binding.chartPieGraph).run {
                data = viewModel.pieGraphData.value
                description.isEnabled = false
                setTouchEnabled(false) // 차트 터치 막기
                setEntryLabelTextSize(11f)
                setEntryLabelColor(Color.BLACK)
                animateXY(1000,1000)
                invalidate()
            }
        }

    }

    fun showParkingLotList(){
        BottomSheetSpinner().setInfo(
            getString(R.string.main_bottomsheet_select_parkinglot_title),
            Constants.parkingLots.map { it.Name },
            onItemClick = {
                if(Constants.parkingLots[it].Name!=Constants.selectedParkingLot.Name){
                    viewModel.setTitle(Constants.parkingLots[it].Name)
                    Constants.selectedParkingLot = Constants.parkingLots[it]
                    viewModel.getMyParkingLotStatistics()
                }
            }).run { show(supportFragmentManager, tag) }
    }

    fun initChart(){
        //barGraph
        (binding.chartBarGraph).run {
            description.isEnabled = false
            setPinchZoom(false) // 두손가락으로 줌 설정
            setDrawValueAboveBar(false) // 입력?값이 차트 위or아래에 그려질 건지 (true=위, false=아래)
            setDrawGridBackground(false) // 격자구조
            setVisibleXRangeMaximum(10f)
            xAxis.run { // 아래 라벨 x축
                isEnabled = true // 라벨 표시 설정
                position = XAxis.XAxisPosition.BOTTOM // 라벨 위치 설정
                setDrawGridLines(false) // 격자구조
                granularity = 1f // 간격 설정
                setDrawAxisLine(false) // 그림
                textSize = 12f // 라벨 크기
                textColor = Color.BLACK
            }
            isEnabled = false
        }

        //pieGraph
        (binding.chartPieGraph).run {
            description.isEnabled = false
            setTouchEnabled(false) // 차트 터치 막기
            setEntryLabelTextSize(11f)
            setEntryLabelColor(Color.BLACK)
        }
    }

    fun setRecyclerView(){
        binding.rvParkingLotLoc.layoutManager = LinearLayoutManager(this)
        binding.rvParkingLotLoc.adapter = ParkingLotSaleAdapter{ position ->

        }
        binding.rvPartnerLoc.layoutManager = LinearLayoutManager(this)
        binding.rvPartnerLoc.adapter = PartnerSaleAdapter{ position ->

        }
    }
    inner class MyXAxisValueFormatter(private val mValues: ArrayList<String>) :
        IAxisValueFormatter {
        override fun getFormattedValue(value: Float, axis: AxisBase): String {

            return if (value <0 || value > mValues.size - 1) {
                ""
            } else mValues[value.toInt()]
        }
    }
    override fun onResume() {
        super.onResume()
        viewModel.getMyParkingLotStatistics()
    }
}