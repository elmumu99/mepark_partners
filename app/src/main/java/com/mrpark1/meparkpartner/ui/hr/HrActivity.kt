package com.mrpark1.meparkpartner.ui.hr

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.LinearLayoutManager
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.databinding.ActivityHrBinding
import com.mrpark1.meparkpartner.ui.Status
import com.mrpark1.meparkpartner.ui.common.BaseActivity
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

        viewModel.partnerUserList.observe(this){
            (binding.rvEmployeeStatus.adapter as PartnerUserAdapter).setPartnerUsers(it)
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
}

