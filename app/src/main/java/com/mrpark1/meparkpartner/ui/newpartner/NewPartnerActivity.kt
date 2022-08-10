package com.mrpark1.meparkpartner.ui.newpartner

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
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
                Status.NEWPART_ACCOUNT_CHECK -> {}

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

        binding.btCheckAccount.setOnClickListener {
            if(viewModel.selectedBank.value == ""){
                snackBar("은행을 선택해주세요")
                return@setOnClickListener
            }
            if(viewModel.bankAccount.value == ""){
                snackBar("계좌번호를 입력해주세요")
                return@setOnClickListener
            }

            val layout = View.inflate(this, R.layout.dialog_check_account, null)

            AlertDialog.Builder(this)
                .setView(layout)
                .setPositiveButton("확인"
                ) { dialog, p1 ->
                    val num = layout.findViewById<EditText>(R.id.et_num).text.toString()
                    val name = layout.findViewById<EditText>(R.id.et_name).text.toString()

                    if(name == ""){
                        snackBar("예금주명을 입력해주세요")
                        return@setPositiveButton
                    }

                    if(num == ""){
                        snackBar("사업자번호 또는 생년월일을 입력해주세요")
                        return@setPositiveButton
                    }

                    viewModel.checkAccount(name,num)

                    dialog.dismiss()
                }
                .setNegativeButton("취소")
                { dialog, p1 ->
                    dialog.dismiss()
                }
                .show()
        }

        viewModel.checkAccountMessage.observe(this){
            snackBar(it)
            if(it=="예금주 확인이 되었습니다."){
                binding.ivCheckAccount.visibility = View.VISIBLE
                binding.btNewPartnerBankSpinner.setOnClickListener {}
                binding.etNewPartnerBank.isEnabled = false
                binding.btCheckAccount.setOnClickListener {}
            }else{
                binding.ivCheckAccount.visibility = View.GONE
            }
        }

    }
}