package com.mrpark1.meparkpartner.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.mrpark1.meparkpartner.R

class LoadingDialog(activity: FragmentActivity) : Dialog(activity) {
    //로딩 다이얼로그

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_loading)
    }

    init {
        setCancelable(false)
    }
}