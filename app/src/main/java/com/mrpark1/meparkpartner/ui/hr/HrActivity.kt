package com.mrpark1.meparkpartner.ui.hr

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.databinding.ActivityHrBinding
import com.mrpark1.meparkpartner.ui.Status
import com.mrpark1.meparkpartner.ui.common.BaseActivity
import com.mrpark1.meparkpartner.util.Constants
import com.mrpark1.meparkpartner.util.ImageUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HrActivity : BaseActivity<ActivityHrBinding>(R.layout.activity_hr) {
    //인사관리 액티비티 (구현 필요)

    private val viewModel: HrViewModel by viewModels()

//    lateinit var commutingHistoryAdapter: CommutingHistoryAdapter
    lateinit var partnerUserAdapter: PartnerUserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        binding.tbHr.setOnMenuItemClickListener {
            if(it.itemId== R.id.menu_hr_setting){
                //세팅화면으로 이동
                startActivity(Intent(this, HrSettingActivity::class.java))
                return@setOnMenuItemClickListener true
            }
            false
        }
        binding.tbHr.setNavigationOnClickListener { finish() }

        binding.rvEmployeeStatus.layoutManager = LinearLayoutManager(this)
        binding.rvEmployeeStatus.adapter = PartnerUserAdapter()





        viewModel.partnerUserList.observe(this){
            (binding.rvEmployeeStatus.adapter as PartnerUserAdapter).setPartnerUsers(it)
        }
        viewModel.workCount.observe(this){
            binding.tvWorkCount.text = it.toString()
        }
        viewModel.leaveCount.observe(this){
            binding.tvLeaveCount.text = it.toString()
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.getMyPartnerUsers()
    }

}

