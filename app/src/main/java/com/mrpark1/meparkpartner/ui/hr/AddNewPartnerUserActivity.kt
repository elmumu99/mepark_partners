package com.mrpark1.meparkpartner.ui.hr

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.data.model.common.PartnerUser
import com.mrpark1.meparkpartner.databinding.ActivityAddNewPartnerUserBinding
import com.mrpark1.meparkpartner.ui.Status
import com.mrpark1.meparkpartner.ui.common.BaseActivity
import com.mrpark1.meparkpartner.ui.dialogs.spinner.BottomSheetSpinner
import com.mrpark1.meparkpartner.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddNewPartnerUserActivity : BaseActivity<ActivityAddNewPartnerUserBinding>(R.layout.activity_add_new_partner_user) {

    private val viewModel: AddNewPartnerUserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.tbAddNewPartnerUser.setNavigationOnClickListener {
            finish()
        }

        viewModel.setMode(intent.getIntExtra("position",-1))

//        tv_2
//        et_name
//        tv_3
//        et_role_spinner
//        iv_role_spinner

        viewModel.currentStatus.observe(this) {
            when (it) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> {
                    loadingDialog.dismiss()
                    Toast.makeText(this,binding.tvTitle.text.toString() + " 성공",Toast.LENGTH_SHORT).show()
                    finish()
                }
                Status.ERROR_INTERNET -> snackBar(getString(R.string.common_error_internet))
                Status.ERROR_EXPIRED -> sessionExpired()
                Status.ERROR -> snackBar(getString(R.string.common_error_unknown))
                Status.ADD_PARTNER_USER ->{
                    binding.tv2.visibility = View.GONE
                    binding.etName.visibility = View.GONE
                    binding.tv3.visibility = View.GONE
                    binding.etRoleSpinner.visibility = View.GONE
                    binding.ivRoleSpinner.visibility = View.GONE

                    binding.tvTitle.text = "신규 직원 등록"

                    binding.btEnter.setOnClickListener {
                        viewModel.setEmail(binding.etEmail.text.toString())
                        viewModel.setSalary(binding.etPay.text.toString())

                        if(viewModel.email.value == ""||viewModel.salary.value == ""||viewModel.ParkingLN.value==""){
                            snackBar("미입력 사항이 있습니다.")
                            return@setOnClickListener
                        }


                        viewModel.addNewEmployee()
                    }
                }
                Status.UPDATE_PARTNER_USER ->{
                    binding.tv2.visibility = View.VISIBLE
                    binding.etName.visibility = View.VISIBLE
                    binding.tv3.visibility = View.VISIBLE
                    binding.etRoleSpinner.visibility = View.VISIBLE
                    binding.ivRoleSpinner.visibility = View.VISIBLE
                    binding.tv6.visibility = View.GONE
                    binding.ivParkingLotSpinner.visibility = View.GONE
                    binding.etParkingLotSpinner.visibility = View.GONE
                    binding.etEmail.visibility = View.GONE
                    binding.tv4.visibility = View.GONE
                    binding.tvTitle.text = "직원 수정"

                    binding.etName.focusable = View.NOT_FOCUSABLE

                    viewModel.setUserInfo(intent.getSerializableExtra("userInfo") as PartnerUser)

                    binding.etName.setText(viewModel.userInfo.value?.Name)
                    if(viewModel.userInfo.value?.Role == "Administrator"){
                        viewModel.setUserRole("최초관리자")
                    }else if(viewModel.userInfo.value?.Role == "SubAdministrator"){
                        viewModel.setUserRole("관리자")
                    }
                    binding.etEmail.setText(viewModel.userInfo.value?.Email)
                    binding.etPay.setText(viewModel.userInfo.value?.Salary)



                    binding.btEnter.setOnClickListener {
                        viewModel.setSalary(binding.etPay.text.toString())

                        if(viewModel.salary.value == "" || viewModel.userInfo.value == null|| viewModel.userInfo.value!!.UserID==""){
                            snackBar("미입력 사항이 있습니다.")
                            return@setOnClickListener
                        }

                        Log.d("TEST@","update ...")
                        viewModel.updateMyEmployee()

                    }
                }
                else -> {}
            }
            if (it != Status.LOADING) loadingDialog.cancel()
        }

        binding.calendarLayout.setOnClickListener {
            DatePickerDialog(
                this,
                R.style.DatePickerDialogTheme,
                { _, sYear, sMonth, sDay ->
                    viewModel.setStartDay(sYear, sMonth, sDay)
                },
                viewModel.year_start.value!!,viewModel.month_start.value!!,viewModel.day_start.value!!
            ).show()
        }


        binding.etRoleSpinner.setOnClickListener{
            val list = resources.getStringArray(R.array.user_role)
            BottomSheetSpinner().setInfo(
                "권한 설정",
                list.asList(),
                onItemClick = {
                    viewModel.setUserRole(list[it])
                },
            ).run { show(supportFragmentManager, tag) }
        }

        binding.etParkingLotSpinner.setOnClickListener{
            val list = arrayListOf<String>()
            for(i in Constants.parkingLots){
                list.add(i.Name)
            }

            BottomSheetSpinner().setInfo(
                "주차장 선택",
                list,
                onItemClick = {
                    viewModel.setParkingLot(list[it])
                    binding.etParkingLotSpinner.setText(list[it])
                },
            ).run { show(supportFragmentManager, tag) }
        }

        viewModel.dateString.observe(this){
            binding.tvStartDay.text = it
        }

        viewModel.userRole.observe(this){
            binding.etRoleSpinner.setText(it.toString())
        }



    }

}