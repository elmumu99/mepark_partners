package com.mrpark1.meparkpartner.ui.newpartner

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.databinding.ActivityNewPartnerBinding
import com.mrpark1.meparkpartner.ui.Status
import com.mrpark1.meparkpartner.ui.common.BaseActivity
import com.mrpark1.meparkpartner.ui.dialogs.spinner.BottomSheetSpinner
import com.mrpark1.meparkpartner.util.ImagePickerUtil
import com.mrpark1.meparkpartner.util.ImageUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NewPartnerActivity : BaseActivity<ActivityNewPartnerBinding>(R.layout.activity_new_partner) {

    private val viewModel: NewPartnerViewModel by viewModels()

    //이미지피커 콜백
    private val imagePickerResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { pickerResult ->
            viewModel.getImageFromPickerResult(pickerResult)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        viewModel.username = intent.getStringExtra("username")!!

        viewModel.currentStatus.observe(this) {
            when (it) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> finish()
                Status.ERROR_INTERNET -> snackBar(getString(R.string.common_error_internet))
                Status.ERROR_EXPIRED -> sessionExpired()
                Status.ERROR -> snackBar(getString(R.string.common_error_unknown))

                else -> {}
            }
            if (it != Status.LOADING) loadingDialog.cancel()
        }

        //파트너 사업자등록증 이미지 업로드
        binding.btNewPartnerPhoto.setOnClickListener {
            ImagePickerUtil.pickImage(this, imagePickerResult)
        }

        //은행 선택 다이얼로그
        binding.btNewPartnerBankSpinner.setOnClickListener {
            val list = resources.getStringArray(R.array.bank_names)
            BottomSheetSpinner().setInfo(
                getString(R.string.new_partner_bottomsheet_title),
                list.asList(),
                onItemClick = {
                    viewModel.selectedBank.value = list[it]
                },
                desc = getString(R.string.new_partner_bottomsheet_desc)
            ).run { show(supportFragmentManager, tag) }
        }

        binding.tbNewPartner.setNavigationOnClickListener { finish() }

    }
}