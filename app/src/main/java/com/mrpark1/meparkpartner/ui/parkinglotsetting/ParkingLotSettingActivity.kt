package com.mrpark1.meparkpartner.ui.parkinglotsetting

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.databinding.ActivityParkingLotSettingBinding
import com.mrpark1.meparkpartner.ui.Status
import com.mrpark1.meparkpartner.ui.common.BaseActivity
import com.mrpark1.meparkpartner.ui.newparkinglot.NewParkingLotActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ParkingLotSettingActivity :
    BaseActivity<ActivityParkingLotSettingBinding>(R.layout.activity_parking_lot_setting) {
    //주차장 설정 액티비티 (주차장 생성 및 수정)

    private val viewModel: ParkingLotSettingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.viewModel = viewModel

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

        //주차장 목록 RecyclerView
        binding.rvParkingLotSetting.layoutManager = LinearLayoutManager(this)
        binding.rvParkingLotSetting.adapter = ParkingLotSettingAdapter {
            startActivity(Intent(this, NewParkingLotActivity::class.java).apply {
                putExtra("parkingLot", it)
            })
        }

        binding.tbParkingLotSetting.setNavigationOnClickListener { finish() }

        //주차장 생성
        binding.tbParkingLotSetting.setOnMenuItemClickListener {
            if (it.itemId == R.id.menu_parkinglot_setting_add) startActivity(
                Intent(this, NewParkingLotActivity::class.java)
            )
            false
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getMyParkingLots()
    }
}