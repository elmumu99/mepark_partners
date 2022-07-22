package com.mrpark1.meparkpartner.ui.managesale

import android.app.Activity
import android.app.DatePickerDialog
import android.view.View
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.databinding.FragmentManageSaleCorporationsBinding
import com.mrpark1.meparkpartner.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageSaleCorporationsFragment :  BaseFragment<FragmentManageSaleCorporationsBinding>(R.layout.fragment_manage_sale_corporations) {

    private val viewModel: ManageSaleViewModel by viewModels()


    override fun init() {
        binding.calendarGroup.setAllOnClickListener{
            DatePickerDialog(
                mContext,
                R.style.DatePickerDialogTheme,
                { _, year, month, day ->
                    viewModel.setStartDate(year,month+1,day)
                    DatePickerDialog(
                        mContext,
                        R.style.DatePickerDialogTheme,
                        { _, _year, _month, _day ->
                            viewModel.setEndDate(_year,_month+1,_day)

                        },
                        viewModel.year_end.value!!,viewModel.month_end.value!!-1,viewModel.day_end.value!!
                    ).show()
                },
                viewModel.year_start.value!!,viewModel.month_start.value!!-1,viewModel.day_start.value!!
            ).show()
        }

        viewModel.start_date.observe(mContext, Observer {
            binding.tvStartDay.text = it
        })
        viewModel.end_date.observe(mContext, Observer {
            binding.tvEndDay.text = it
        })

    }


    fun Group.setAllOnClickListener(listener : View.OnClickListener?){
        referencedIds.forEach { id ->
            mContext.findViewById<View>(id).setOnClickListener(listener)
        }
    }
}