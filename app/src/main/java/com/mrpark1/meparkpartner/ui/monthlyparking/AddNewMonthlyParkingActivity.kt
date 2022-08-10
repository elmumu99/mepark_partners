package com.mrpark1.meparkpartner.ui.monthlyparking

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.viewModels
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.databinding.ActivityAddNewMonthlyParkingBinding
import com.mrpark1.meparkpartner.ui.Status
import com.mrpark1.meparkpartner.ui.common.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddNewMonthlyParkingActivity : BaseActivity<ActivityAddNewMonthlyParkingBinding>(R.layout.activity_add_new_monthly_parking) {

    private val viewModel: AddNewMonthlyParkingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.currentStatus.observe(this) {
            when (it) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> {
                    Toast.makeText(this,"월주차 등록 성공!",Toast.LENGTH_LONG).show()
                    finish()
                }
                Status.ERROR_INTERNET -> snackBar(getString(R.string.common_error_internet))
                Status.ERROR_EXPIRED -> sessionExpired()
                Status.ERROR -> snackBar(getString(R.string.common_error_unknown))
                else -> {}
            }
            if (it != Status.LOADING) loadingDialog.cancel()
        }
        binding.calendarLayout.setOnClickListener {
            DatePickerDialog(
                this,
                R.style.DatePickerDialogTheme,
                { _, sYear, sMonth, sDay ->
                    DatePickerDialog(
                        this,
                        R.style.DatePickerDialogTheme,
                        { _, eYear, eMonth, eDay ->
                            viewModel.setDate(sYear, sMonth, sDay,eYear, eMonth, eDay)
                        },
                        viewModel.year_end.value!!,viewModel.month_end.value!!,viewModel.day_end.value!!
                    ).show()
                },
                viewModel.year_start.value!!,viewModel.month_start.value!!,viewModel.day_start.value!!
            ).show()
        }

        binding.rgEnterCartype.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.rb_enter_big ->{ viewModel.setCarType("Big") }
                R.id.rb_enter_small ->{ viewModel.setCarType("Small") }
            }
        }

        binding.btEnter.setOnClickListener {
            val lp = binding.etMonthlyParkingLp.text.toString()
            val contact = binding.etMonthlyParkingContact.text.toString()
            val memo = binding.etMonthlyParkingMemo.text.toString()
            val profit = binding.etMonthlyParkingPrice.text.toString()
            if(lp == ""||contact == ""|| memo == ""|| profit==""){
                snackBar("미입력 항목이 있습니다.")
                return@setOnClickListener
            }

            viewModel.setLP(lp)
            viewModel.setContact(contact)
            viewModel.setMemo(memo)
            viewModel.setProfit(profit)

            viewModel.addNewMonthlyParkingCar()
        }

        viewModel.start_date.observe(this){
            binding.tvStartDay.text = it
        }
        viewModel.end_date.observe(this){
            binding.tvEndDay.text = it
        }
    }
}