package com.mrpark1.meparkpartner.ui.newpartner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.databinding.ActivityCheckAccountBinding
import com.mrpark1.meparkpartner.ui.common.BaseActivity

class CheckAccountActivity : BaseActivity<ActivityCheckAccountBinding>(R.layout.activity_check_account) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bank = intent.getStringExtra("Bank")
        val accountNum = intent.getStringExtra("AccountNum")
        var bankNum = ""

    }



}