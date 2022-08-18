package com.mrpark1.meparkpartner.ui.monthlyparking

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.databinding.ActivityMonthlyParkingBinding
import com.mrpark1.meparkpartner.ui.Status
import com.mrpark1.meparkpartner.ui.common.BaseActivity
import com.mrpark1.meparkpartner.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MonthlyParkingActivity : BaseActivity<ActivityMonthlyParkingBinding>(R.layout.activity_monthly_parking) {

    private val viewModel: MonthlyParkingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initRecycler()

        viewModel.currentStatus.observe(this) {
            when (it) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> {}
                Status.ERROR_INTERNET -> snackBar(getString(R.string.common_error_internet))
                Status.ERROR_EXPIRED -> sessionExpired()
                Status.ERROR -> snackBar(getString(R.string.common_error_unknown))
                else -> {}
            }
            if (it != Status.LOADING) loadingDialog.cancel()
        }

        binding.tlMonthlyParkingTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab?.position

                if(position==0){ //계약중
                    binding.rvMonthlyParkingDoing.visibility = View.VISIBLE
                    binding.rvMonthlyParkingDone.visibility = View.GONE
                    binding.tvAddMonthlyParking.visibility = View.VISIBLE

                    viewModel.setMode("1")
                }else{ //계약만료
                    binding.rvMonthlyParkingDoing.visibility = View.GONE
                    binding.rvMonthlyParkingDone.visibility = View.VISIBLE
                    binding.tvAddMonthlyParking.visibility = View.GONE

                    viewModel.setMode("2")
                }

                viewModel.getMonthParkedCars()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })


        binding.tvAddMonthlyParking.setOnClickListener {
            //TODO Move To AddMonthlyParkingActivity
            startActivity(Intent(this,AddNewMonthlyParkingActivity::class.java))
        }

        binding.tbMonthlyParking.setNavigationOnClickListener { finish() }

        viewModel.mode.observe(this){
            Log.d("TEST@","mode:: $it")
        }

        viewModel.carList.observe(this){
            (binding.rvMonthlyParkingDoing.adapter as MonthlyParkingDoingAdapter).setmonthlyParkingList(it)
        }
        viewModel.carDoneList.observe(this){
            (binding.rvMonthlyParkingDone.adapter as MonthlyParkingDoneAdapter).setmonthlyParkingList(it)
        }
    }

    fun initRecycler(){
        binding.rvMonthlyParkingDoing.layoutManager = LinearLayoutManager(this)
        binding.rvMonthlyParkingDone.layoutManager = LinearLayoutManager(this)

        binding.rvMonthlyParkingDoing.adapter = MonthlyParkingDoingAdapter{ car ->
            startActivity(Intent(this,AddNewMonthlyParkingActivity::class.java).apply {
                putExtra("car",car)
                putExtra("mode","update")
            })
        }
        binding.rvMonthlyParkingDone.adapter = MonthlyParkingDoneAdapter()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getMonthParkedCars()
    }
}