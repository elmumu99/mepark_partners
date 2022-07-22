package com.mrpark1.meparkpartner.ui.parkhistory

import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableArrayList
import androidx.recyclerview.widget.RecyclerView
import com.mrpark1.meparkpartner.data.model.common.Car

@BindingAdapter("bindParkHistoryCars")
fun bindParkHistoryCars(r: RecyclerView, cars: ObservableArrayList<Car>?) {
    val adapter: ParkHistoryAdapter? = r.adapter as ParkHistoryAdapter?
    if (adapter == null || cars == null) return
    adapter.submitList(cars.toList())
}