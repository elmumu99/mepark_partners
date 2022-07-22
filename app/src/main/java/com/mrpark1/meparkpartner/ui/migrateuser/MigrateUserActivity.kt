package com.mrpark1.meparkpartner.ui.migrateuser

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.databinding.ActivityMigrateUserBinding
import com.mrpark1.meparkpartner.ui.Status
import com.mrpark1.meparkpartner.ui.common.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MigrateUserActivity :
    BaseActivity<ActivityMigrateUserBinding>(R.layout.activity_migrate_user) {
    //계정 찾기 액티비티 (기기 변경, 이메일 변동 시 계정 마이그레이션)

    private val viewModel by viewModels<MigrateUserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        viewModel.idToken = intent.getStringExtra("IDT")!!

        viewModel.currentStatus.observe(this) {
            when (it) {
                Status.MIGRATE_AUTH_CODE_SENT -> snackBar(getString(R.string.migrate_user_authcode_sent))
                Status.SUCCESS -> {
                    setResult(RESULT_OK, Intent().putExtra("success", true))
                    finish()
                }
                Status.LOADING -> loadingDialog.show()
                Status.ERROR -> snackBar(getString(R.string.common_error_unknown))
                Status.ERROR_INTERNET -> snackBar(getString(R.string.common_error_internet))
                Status.ERROR_EXPIRED -> sessionExpired()
                Status.MIGRATE_ERROR_WRONG_AUTH_CODE -> snackBar(getString(R.string.migrate_user_wrong_authcode))
                Status.MIGRATE_ERROR_INVALID_EMAIL -> snackBar(getString(R.string.migrate_user_invalid_email))
                else -> {}
            }
            if (it != Status.LOADING) loadingDialog.cancel()
        }

        binding.tbMigrateUser.setNavigationOnClickListener { finish() }
    }
}