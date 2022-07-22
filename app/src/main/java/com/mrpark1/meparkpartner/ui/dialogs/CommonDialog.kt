package com.mrpark1.meparkpartner.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.databinding.DialogCommonBinding

class CommonDialog(
    activity: FragmentActivity,
    title: String? = null,
    message: String,
    positiveText: String = activity.getString(R.string.common_ok),
    cancelable: Boolean = true,
    private val onPositive: () -> Unit = {}
) : Dialog(activity) {
    //일반 다이얼로그, 취소 버튼 여부 설정 가능

    private lateinit var binding: DialogCommonBinding
    private val viewModel: CommonDialogViewModel by activity.viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_common, null, false)
        setContentView(binding.root)
        binding.viewModel = viewModel

        binding.btCommonDialogPositive.setOnClickListener {
            onPositive()
            cancel()
        }
        binding.btCommonDialogNegative.setOnClickListener { cancel() }
    }

    init {
        viewModel.title = title
        viewModel.message = message
        viewModel.positiveText = positiveText
        viewModel.negativeText = activity.getString(R.string.common_cancel)
        viewModel.cancelable = cancelable
        setCancelable(cancelable)
    }

}