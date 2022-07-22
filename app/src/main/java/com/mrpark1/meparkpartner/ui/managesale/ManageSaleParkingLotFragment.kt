package com.mrpark1.meparkpartner.ui.managesale

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.data.model.common.ParkingLot
import com.mrpark1.meparkpartner.databinding.FragmentManageSaleParkingLotBinding
import com.mrpark1.meparkpartner.ui.common.BaseFragment
import com.mrpark1.meparkpartner.ui.dialogs.spinner.BottomSheetSpinner
import com.mrpark1.meparkpartner.ui.main.MainViewModel
import com.mrpark1.meparkpartner.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageSaleParkingLotFragment : BaseFragment<FragmentManageSaleParkingLotBinding>(R.layout.fragment_manage_sale_parking_lot) {

    private val viewModel : ManageSaleViewModel by viewModels()

    private lateinit var selectedParkingLot : ParkingLot



    fun Group.setAllOnClickListener(listener : View.OnClickListener?){
        referencedIds.forEach { id ->
            mContext.findViewById<View>(id).setOnClickListener(listener)
        }
    }

    override fun init() {
        selectedParkingLot = Constants.selectedParkingLot
        binding.tvParkingLotTitle.text = selectedParkingLot.Name

        binding.btSelectParkingLot.setOnClickListener {
            val list = ArrayList<String>()
            val parkingLots = Constants.parkingLots
            for(parkingLot in parkingLots){
                list.add(parkingLot.Name)
            }
            BottomSheetSpinner().setInfo(
                getString(R.string.enter_bottomsheet_title_visitplace),
                list,
                onItemClick = {
                    for(parkingLot in parkingLots){
                        if(list[it].equals(parkingLot.Name)){
                            selectedParkingLot = parkingLot
                            binding.tvParkingLotTitle.text = selectedParkingLot.Name
                        }
                    }
                }
            ).run { show(mContext.supportFragmentManager, tag) }
        }

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




}