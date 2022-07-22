package com.mrpark1.meparkpartner.ui.managesale

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ManageSaleViewModel @Inject constructor() : ViewModel() {

    private val cal = Calendar.getInstance()

    val year_start = MutableLiveData<Int>(cal.get(Calendar.YEAR))
    val month_start = MutableLiveData<Int>(cal.get(Calendar.MONTH)+1)
    val day_start = MutableLiveData<Int>(cal.get(Calendar.DAY_OF_MONTH))
    val start_date = MutableLiveData<String>(
        "${cal.get(Calendar.YEAR)}년" +
                "${(cal.get(Calendar.MONTH) + 1)}월" +
                "${cal.get(Calendar.DAY_OF_MONTH)}일")


    val year_end = MutableLiveData<Int>(cal.get(Calendar.YEAR))
    val month_end = MutableLiveData<Int>(cal.get(Calendar.MONTH)+1)
    val day_end = MutableLiveData<Int>(cal.get(Calendar.DAY_OF_MONTH))
    val end_date = MutableLiveData<String>(
        "${cal.get(Calendar.YEAR)}년" +
                "${(cal.get(Calendar.MONTH) + 1)}월" +
                "${cal.get(Calendar.DAY_OF_MONTH)}일")


    fun setStartDate(year: Int, month: Int, day: Int){
        this.year_start.value = year
        this.month_start.value = month
        this.day_start.value = day
        start_date.value = ""+year+"년"+month+"월"+day+"일"
        Log.d("TEST@","startDate :: ${start_date.value}")
    }

    fun setEndDate(year: Int, month: Int, day: Int){
        this.year_end.value = year
        this.month_end.value = month
        this.day_end.value = day
        end_date.value = ""+year+"년"+month+"월"+day+"일"
        Log.d("TEST@","endDate :: ${end_date.value}")
    }
}