package com.mrpark1.meparkpartner.ui.monthlyparking

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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

    private val monthlyParkingViewModel: MonthlyParkingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initRecycler()

        binding.tlMonthlyParkingTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab?.position

                if(position==0){ //계약중
                    binding.rvMonthlyParkingDoing.visibility = View.VISIBLE
                    binding.rvMonthlyParkingDone.visibility = View.GONE
                    binding.tvAddMonthlyParking.visibility = View.VISIBLE
                }else{ //계약만료
                    binding.rvMonthlyParkingDoing.visibility = View.GONE
                    binding.rvMonthlyParkingDone.visibility = View.VISIBLE
                    binding.tvAddMonthlyParking.visibility = View.GONE
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })


        binding.tvAddMonthlyParking.setOnClickListener {
            //TODO Move To AddMonthlyParkingActivity
        }
    }

    fun initRecycler(){
        binding.rvMonthlyParkingDoing.layoutManager = LinearLayoutManager(this)
        binding.rvMonthlyParkingDone.layoutManager = LinearLayoutManager(this)


    }
}