package com.mrpark1.meparkpartner.ui.charge

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.data.model.common.ParkingLot
import com.mrpark1.meparkpartner.databinding.ActivityChargeBinding
import com.mrpark1.meparkpartner.ui.Status
import com.mrpark1.meparkpartner.ui.common.BaseActivity
import com.mrpark1.meparkpartner.ui.managesale.ManageSaleViewModel
import com.mrpark1.meparkpartner.util.CommandUtil
import com.mrpark1.meparkpartner.util.SharedPrefUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ChargeActivity : BaseActivity<ActivityChargeBinding>(R.layout.activity_charge) {
    private val viewModel: ChargeViewModel by viewModels()


    @Inject
    lateinit var sharedPrefUtil: SharedPrefUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //init recycler
        initRecycler()
        //현재 날짜와 사용자 이름 보여줌
        binding.date =
            "${viewModel.cal.get(Calendar.YEAR)}년 ${viewModel.cal.get(Calendar.MONTH) + 1}월 ${viewModel.cal.get(Calendar.DAY_OF_MONTH)}일"
        binding.name = sharedPrefUtil.getString(SharedPrefUtil.KEY_USER_NAME, "파트너") + " 관리자님"



        //예치금 충전 페이지
        binding.btChargeFill.setOnClickListener {
            startActivity(Intent(this, FillPointActivity::class.java).apply {
                putExtra("name", sharedPrefUtil.getString(SharedPrefUtil.KEY_USER_NAME, "파트너"))
                putExtra("parkingLot", intent.getSerializableExtra("parkingLot") as ParkingLot)
            })
        }

        binding.tbCharge.setNavigationOnClickListener { finish() }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{ //탭 이동부분
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab?.position

                viewModel.setCurrentPosition(position!!)

                if(position==0){
                    binding.rvChargingHistory.visibility = View.VISIBLE
                    binding.rvPointHistory.visibility = View.GONE
                }else{
                    binding.rvChargingHistory.visibility = View.GONE
                    binding.rvPointHistory.visibility = View.VISIBLE
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) { }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        viewModel.currentStatus.observe(this) {
            when (it) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> loadingDialog.dismiss()
                Status.ERROR_INTERNET -> snackBar(getString(R.string.common_error_internet))
                Status.ERROR_EXPIRED -> sessionExpired()
                Status.ERROR -> snackBar(getString(R.string.common_error_unknown))
                else -> {}
            }
            if (it != Status.LOADING) loadingDialog.cancel()
        }

        binding.calendarGroup.setAllOnClickListener{
            DatePickerDialog(
                this,
                R.style.DatePickerDialogTheme,
                { _, sYear, sMonth, sDay ->
                    DatePickerDialog(
                        this,
                        R.style.DatePickerDialogTheme,
                        { _, eYear, eMonth, eDay ->
                            viewModel.setDate(sYear, sMonth+1, sDay,eYear, eMonth+1, eDay)
                        },
                        viewModel.year_end.value!!,viewModel.month_end.value!!-1,viewModel.day_end.value!!
                    ).show()
                },
                viewModel.year_start.value!!,viewModel.month_start.value!!-1,viewModel.day_start.value!!
            ).show()
        }

        binding.btAccidentReception.setOnClickListener {
            startActivity(Intent(this,AccidentReceptionActivity::class.java))
        }
        viewModel.start_date.observe(this){
            binding.tvStartDay.text = it
        }
        viewModel.end_date.observe(this){
            binding.tvEndDay.text = it
        }

        viewModel.chargingHistory.observe(this){
            (binding.rvChargingHistory.adapter as ChargingHistoryAdapter).setChargingHistoryList(it)
        }

        viewModel.pointHistory.observe(this){
            (binding.rvPointHistory.adapter as PointHistoryAdapter).setpointHistoryList(it)
        }

        viewModel.ACount.observe(this){
               binding.tvACount.text = it.toString()
        }
        viewModel.BCount.observe(this){
            binding.tvBCount.text = it.toString()
        }
        viewModel.NCount.observe(this){
            binding.tvNCount.text = it.toString()
        }
        viewModel.TotalCount.observe(this){
            binding.tvTotalCount.text = it.toString()
        }

        viewModel.point.observe(this){
            binding.tvPoint.text = "남은 예치금: ${CommandUtil.addCommaNumInt(it.toString())} 원"
        }

        viewModel.TotalUsageAmount.observe(this){
            binding.tvTotalInsurance.text = "총 보험료 : ${CommandUtil.addCommaNumInt(it.toString())} 원"
        }


    }

    override fun onResume() {
        super.onResume()
        viewModel.initData()
    }

    fun initRecycler(){
        binding.rvChargingHistory.layoutManager = LinearLayoutManager(this@ChargeActivity)
        binding.rvChargingHistory.adapter = ChargingHistoryAdapter()

        binding.rvPointHistory.layoutManager = LinearLayoutManager(this@ChargeActivity)
        binding.rvPointHistory.adapter = PointHistoryAdapter()
    }

    fun Group.setAllOnClickListener(listener : View.OnClickListener?){
        referencedIds.forEach { id ->
            rootView.findViewById<View>(id).setOnClickListener(listener)
        }
    }
}

