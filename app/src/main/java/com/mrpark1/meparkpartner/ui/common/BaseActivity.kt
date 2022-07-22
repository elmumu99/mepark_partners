package com.mrpark1.meparkpartner.ui.common

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.ui.dialogs.LoadingDialog
import com.mrpark1.meparkpartner.ui.start.StartActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class BaseActivity<D : ViewDataBinding>(@LayoutRes private val layoutResId: Int) :
    AppCompatActivity() {
    protected lateinit var binding: D
    protected lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutResId)
        binding.lifecycleOwner = this@BaseActivity
        loadingDialog = LoadingDialog(this)
    }

    //스낵바
    protected fun snackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).show()
    }

    protected fun snackBar(id: Int) {
        Snackbar.make(binding.root, getString(id), Snackbar.LENGTH_SHORT)
            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).show()
    }

    //토큰 만료시 스플래시 화면
    protected fun sessionExpired() {
        Toast.makeText(this, "장시간 접속하여 다시 로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
        finishAffinity()
        startActivity(Intent(this, StartActivity::class.java))
    }

    //뒤로가기 활성화/비활성화
    protected var backPressEnabled = true
    private var backPressedOnce = false

    override fun onBackPressed() {
        when {
            backPressEnabled -> super.onBackPressed()
            backPressedOnce -> finishAffinity()
            else -> {
                Toast.makeText(this, getString(R.string.common_back_press), Toast.LENGTH_SHORT)
                    .show()
                backPressedOnce = true
                lifecycleScope.launch {
                    delay(3000)
                    backPressedOnce = false
                }
            }
        }
    }
}
