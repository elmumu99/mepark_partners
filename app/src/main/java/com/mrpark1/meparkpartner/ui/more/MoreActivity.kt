package com.mrpark1.meparkpartner.ui.more

import android.content.Intent
import android.os.Bundle
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.data.model.common.ParkingLot
import com.mrpark1.meparkpartner.databinding.ActivityMoreBinding
import com.mrpark1.meparkpartner.ui.common.BaseActivity
import com.mrpark1.meparkpartner.ui.enter.EnterActivity
import com.mrpark1.meparkpartner.ui.monthlyparking.AddNewMonthlyParkingActivity
import com.mrpark1.meparkpartner.ui.monthlyparking.MonthlyParkingActivity
import com.mrpark1.meparkpartner.ui.park.ParkActivity
import com.mrpark1.meparkpartner.ui.parkhistory.ParkHistoryActivity

class MoreActivity : BaseActivity<ActivityMoreBinding>(R.layout.activity_more) {
    //더보기 액티비티 (바텀내비처럼 생긴 그것에만 들어있음. 어디에 쓸지는 잘 모름)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.tbMore.setNavigationOnClickListener { finish() }

        binding.bnMore.selectedItemId = R.id.bottom_nav_more
        binding.bnMore.setOnItemSelectedListener {
            if (it.itemId == R.id.bottom_nav_more) return@setOnItemSelectedListener false
            startActivity(
                Intent(
                    this, when (it.itemId) {
                        R.id.bottom_nav_enter -> EnterActivity::class.java
                        R.id.bottom_nav_park -> ParkActivity::class.java
                        R.id.bottom_nav_park_history -> ParkHistoryActivity::class.java
                        else -> MoreActivity::class.java
                    }
                ).apply {
                    putExtra("parkingLot", intent.getSerializableExtra("parkingLot") as ParkingLot)
                })
            finish()
            false
        }

        binding.tvMonthlyParking.setOnClickListener {
            startActivity(Intent(this,MonthlyParkingActivity::class.java))
        }
    }
}