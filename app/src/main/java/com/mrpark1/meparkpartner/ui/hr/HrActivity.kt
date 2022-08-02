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

//        binding.calendarGroup.setAllOnClickListener{
//            DatePickerDialog(
//                this,
//                R.style.DatePickerDialogTheme,
//                { _, year, month, day ->
//                    viewModel.setDate(year,month+1,day)
//
//                    //viewModel.getMyPartnerUsersCommutingHistory()
//                },
//                viewModel.year.value!!,viewModel.month.value!!-1,viewModel.day.value!!
//            ).show()
//        }
//
//        viewModel.date.observe(this){
//            binding.tvDate.text = it
//        }
//
//        commutingHistoryAdapter = CommutingHistoryAdapter(this)
//        binding.rvEmployeeStatus.adapter = commutingHistoryAdapter
//        commutingHistoryAdapter.setPartnerUsers(viewModel.partnerUserList)
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



        binding.tvHr.setOnClickListener {
            Glide.with(this).load(getQrCodeBitmap(Constants.selectedParkingLot.ParkingLN)).into(binding.ivQr)
            binding.ivQr.visibility = View.VISIBLE
        }

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

    fun Group.setAllOnClickListener(listener : View.OnClickListener?){
        referencedIds.forEach { id ->
            rootView.findViewById<View>(id).setOnClickListener(listener)
        }
    }

    fun getQrCodeBitmap(text: String): Bitmap {
        val size = 512 //pixels
        val hints = hashMapOf<EncodeHintType, Int>().also {
            it[EncodeHintType.MARGIN] = 1
        } // Make the QR code buffer border narrower
        val bits = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size)
        return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also {
            for (x in 0 until size) {
                for (y in 0 until size) {
                    it.setPixel(x, y, if (bits[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        }
    }
}

