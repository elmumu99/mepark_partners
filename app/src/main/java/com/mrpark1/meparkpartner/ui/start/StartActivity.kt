package com.mrpark1.meparkpartner.ui.start

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.databinding.ActivityStartBinding
import com.mrpark1.meparkpartner.ui.Status
import com.mrpark1.meparkpartner.ui.common.BaseActivity
import com.mrpark1.meparkpartner.ui.main.MainActivity
import com.mrpark1.meparkpartner.ui.newuser.NewUserActivity
import com.mrpark1.meparkpartner.ui.nopartner.NoPartnerActivity
import com.mrpark1.meparkpartner.ui.onboard.OnboardActivity
import com.mrpark1.meparkpartner.util.OneTapCoroutine
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StartActivity : BaseActivity<ActivityStartBinding>(R.layout.activity_start) {
    //스플래시 (구글 로그인) 화면

    private val viewModel: StartViewModel by viewModels()

    private val oneTapUtil = OneTapCoroutine(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        backPressEnabled = false

        //원탭 로그인 유틸 초기화
        viewModel.oneTapCoroutine = oneTapUtil

        viewModel.currentStatus.observe(this) {
            when (it) {
                Status.START_FIRST_LAUNCH -> startActivity(Intent(this, OnboardActivity::class.java))
                Status.START_NEW_USER -> startActivity(
                    Intent(this, NewUserActivity::class.java).putExtra("IDT", viewModel.idToken)
                )
                Status.START_NO_PARTNER -> startActivity(
                    Intent(this, NoPartnerActivity::class.java).putExtra("user", viewModel.user)
                )
                Status.SUCCESS -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                Status.ERROR -> snackBar(getString(R.string.common_error_unknown))
                Status.ERROR_INTERNET -> snackBar(getString(R.string.common_error_internet))
                Status.START_ERROR_ONE_TAP_CANCELED -> snackBar(getString(R.string.start_error_onetap_canceled))
                Status.START_ERROR_NO_GOOGLE -> snackBar(getString(R.string.start_error_no_google))
                else -> {}
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.startLogic()
    }
}