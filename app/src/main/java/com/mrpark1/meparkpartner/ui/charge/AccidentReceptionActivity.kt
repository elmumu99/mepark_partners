package com.mrpark1.meparkpartner.ui.charge

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebSettings
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.databinding.ActivityAccidentReceptionBinding
import com.mrpark1.meparkpartner.ui.common.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccidentReceptionActivity : BaseActivity<ActivityAccidentReceptionBinding>(R.layout.activity_accident_reception) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.wvAccidentReception.run {
            settings.run {
                @SuppressLint("SetJavaScriptEnabled")
                javaScriptEnabled = true
                domStorageEnabled = true
                setSupportMultipleWindows(true)
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
        }

        binding.wvAccidentReception.loadUrl("https://simg.kr/mepark/")
    }

}