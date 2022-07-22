package com.mrpark1.meparkpartner.ui.newuser

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.databinding.ActivityNewUserBinding
import com.mrpark1.meparkpartner.ui.Status
import com.mrpark1.meparkpartner.ui.common.BaseActivity
import com.mrpark1.meparkpartner.ui.dialogs.LoadingDialog
import com.mrpark1.meparkpartner.ui.migrateuser.MigrateUserActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewUserActivity : BaseActivity<ActivityNewUserBinding>(R.layout.activity_new_user) {
    //회원가입 (정보 입력) 액티비티

    private val viewModel: NewUserViewModel by viewModels()

    //계정 찾기 성공 시 회원가입 화면 종료
    private val migrateResult = registerForActivityResult(StartActivityForResult()) { result ->
        val data = result.data ?: return@registerForActivityResult
        if (data.getBooleanExtra("success", false)) finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        backPressEnabled = false

        loadingDialog = LoadingDialog(this)

        viewModel.idToken = intent.getStringExtra("IDT")!!

        viewModel.currentStatus.observe(this) {
            when (it) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> finish()
                Status.ERROR_INTERNET -> snackBar(getString(R.string.common_error_internet))
                Status.ERROR -> snackBar(getString(R.string.common_error_unknown))
                else -> {}
            }
            if (it != Status.LOADING) loadingDialog.cancel()
        }

        //이미 계정이 있을 시 (계정 찾기)
        binding.btNewUserMigration.setOnClickListener {
            migrateResult.launch(
                Intent(this, MigrateUserActivity::class.java)
                    .putExtra("IDT", viewModel.idToken)
            )
        }
    }
}