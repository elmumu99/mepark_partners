package com.mrpark1.meparkpartner.ui.nopartner

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.data.model.common.User
import com.mrpark1.meparkpartner.databinding.ActivityNoPartnerBinding
import com.mrpark1.meparkpartner.ui.Status
import com.mrpark1.meparkpartner.ui.common.BaseActivity
import com.mrpark1.meparkpartner.ui.dialogs.CommonDialog
import com.mrpark1.meparkpartner.ui.main.MainActivity
import com.mrpark1.meparkpartner.ui.newpartner.NewPartnerActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoPartnerActivity : BaseActivity<ActivityNoPartnerBinding>(R.layout.activity_no_partner) {
    //소속 파트너가 없을 시 보여지는 화면 (파트너 생성 또는 초대 수락 가능)

    private val viewModel: NoPartnerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        //뒤로가기 방지
        backPressEnabled = false

        viewModel.user.value = intent.getSerializableExtra("user") as User

        viewModel.currentStatus.observe(this) {
            when (it) {
                Status.LOADING -> loadingDialog.show()
                Status.NOPART_INVITED -> {
                    snackBar(getString(R.string.no_partner_invited))
                    viewModel.setInvitation()
                }
                Status.NOPART_PENDING -> {
                    //파트너사 생성 대기 상태
                }
                Status.SUCCESS -> startActivity(Intent(this, MainActivity::class.java))
                Status.NOPART_NO_PARTNER -> {}
                Status.ERROR -> snackBar(getString(R.string.common_error_unknown))
                Status.ERROR_INTERNET -> snackBar(getString(R.string.common_error_internet))
                Status.ERROR_EXPIRED -> sessionExpired()
                else -> {}
            }
            if (it != Status.LOADING) loadingDialog.cancel()
        }

        //파트너 생성
        binding.btNoPartnerNewPartner.setOnClickListener {
            startActivity(Intent(this, NewPartnerActivity::class.java).apply {
                putExtra("username", viewModel.user.value!!.Name)
            })
        }

        binding.ivCloseInvitation.setOnClickListener {
            CommonDialog(this,viewModel.invitation_name.value,"초대를 숨기시겠습니까 ?","숨기기",true) {
                viewModel.rejectInvitation()
            }.show()
        }


        binding.btAcceptInvitation.setOnClickListener {
            CommonDialog(this,viewModel.invitation_name.value,"정말로 수락하시겠습니까?","수락",true) {
                viewModel.acceptInvitation()
            }.show()
        }

        viewModel.isVisible.observe(this){
            if(it){
                binding.layoutNoPartnerNoInvitation.visibility = View.VISIBLE
            }else{
                binding.layoutNoPartnerNoInvitation.visibility = View.GONE
            }
        }

        viewModel.invitation_name.observe(this){
            binding.tvInvitationName.text = it
        }
        viewModel.start_day.observe(this){
            binding.tvStartDay.text = it
        }
        viewModel.salary.observe(this){
            binding.tvPayment.text = it
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshUser()
    }
}