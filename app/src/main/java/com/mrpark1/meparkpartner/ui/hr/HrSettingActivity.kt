package com.mrpark1.meparkpartner.ui.hr

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.databinding.ActivityHrSettingBinding
import com.mrpark1.meparkpartner.ui.common.BaseActivity
import com.mrpark1.meparkpartner.ui.dialogs.CommonDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HrSettingActivity : BaseActivity<ActivityHrSettingBinding>(R.layout.activity_hr_setting) {

    private val viewModel: HrViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.rvPartnerUserList.layoutManager = LinearLayoutManager(this)
        binding.rvPartnerUserList.adapter = SettingPartnerUserAdapter(onItemClick = { position, action->
            if(viewModel.partnerUserList.value?.get(position)?.Role=="Administrator"){
                snackBar("최초관리자는 수정하거나 삭제할 수 없습니다.")
                return@SettingPartnerUserAdapter
            }
            when(action){
                //직원 수정
                0 ->{
                    //TODO 직원 수정 화면으로 이동
                    startActivity(Intent(this,AddNewPartnerUserActivity::class.java).apply {
                        putExtra("position",position)
                        putExtra("userInfo",viewModel.partnerUserList.value?.get(position))
                    })

                }

                //직원 삭제
                1 ->{
                    CommonDialog(this,
                        title = "직원 삭제",
                        message = "정말로 삭제하시겠습니까?",
                        cancelable = true,
                        onPositive = { viewModel.deletePartnerUser(position) }).show()
                }
            }
        })

        viewModel.partnerUserList.observe(this){
            (binding.rvPartnerUserList.adapter as SettingPartnerUserAdapter).setPartnerUsers(it)
        }

        binding.tvAddPartnerUser.setOnClickListener {
            startActivity(Intent(this,AddNewPartnerUserActivity::class.java))
        }

        binding.tvEditUser.setOnClickListener {
            //edit mode on/off
            viewModel.changeEditMode()
        }

        viewModel.isEditMode.observe(this){
            if(it){ //편집 모드
                (binding.rvPartnerUserList.adapter as SettingPartnerUserAdapter).setIsEditMode(true)
                binding.tvAddPartnerUser.visibility = View.GONE
                binding.tvTitle.text = "편집"
            }else{ //편집모드 X
                (binding.rvPartnerUserList.adapter as SettingPartnerUserAdapter).setIsEditMode(false)
                binding.tvAddPartnerUser.visibility = View.VISIBLE
                binding.tvTitle.text = "직원 설정"
            }
        }

        binding.tbHtSetting.setNavigationOnClickListener {
            finish()
        }


    }

    override fun onResume() {
        super.onResume()
        viewModel.getMyPartnerUsers()
    }
}