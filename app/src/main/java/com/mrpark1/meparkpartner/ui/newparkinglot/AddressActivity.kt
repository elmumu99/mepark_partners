package com.mrpark1.meparkpartner.ui.newparkinglot

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.databinding.ActivityAddressBinding
import com.mrpark1.meparkpartner.ui.common.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddressActivity : BaseActivity<ActivityAddressBinding>(R.layout.activity_address) {
    //주차장 생성 시 주소 입력을 위한 카카오 도로명주소 API 웹뷰

    private lateinit var client: WebViewClient
    private lateinit var chromeClient: WebChromeClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        client = object : WebViewClient() {
            //override something
        }

        chromeClient = object : WebChromeClient() {
            //override something
        }

        binding.wvAddress.run {
            settings.run {
                @SuppressLint("SetJavaScriptEnabled")
                javaScriptEnabled = true
                domStorageEnabled = true
            }
            webViewClient = client
            webChromeClient = chromeClient
            addJavascriptInterface(WebAppInterface(), "Android")
            loadUrl("https://com-mrpark1-meparkpartner.web.app/")
        }

        binding.tbAddress.setNavigationOnClickListener { finish() }
    }

    override fun onBackPressed() {
        if (binding.wvAddress.canGoBack()) binding.wvAddress.goBack()
        else super.onBackPressed()
    }

    //브릿지 처리
    inner class WebAppInterface {
        @JavascriptInterface
        fun processAddress(address: String?) {
            Intent().apply {
                putExtra("address", address)
                setResult(RESULT_OK, this)
            }
            finish()
        }
    }
}