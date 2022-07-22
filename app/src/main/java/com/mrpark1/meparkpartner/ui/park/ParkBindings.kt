package com.mrpark1.meparkpartner.ui.park

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableArrayList
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.data.model.common.Car

//RecyclerView 구현
@BindingAdapter("bindParkCars")
fun bindParkCars(r: RecyclerView, cars: ObservableArrayList<Car>?) {
    val adapter: ParkAdapter? = r.adapter as ParkAdapter?
    if (adapter == null || cars == null) return
    adapter.submitList(cars.toList())
}

//화면 (차량 입출차 뱃지) 구현
@BindingAdapter("bindParkChipParkStatus")
fun bindParkChipStatus(textView: TextView, onExit: Boolean) {
    if (onExit) {
        textView.backgroundTintList = ContextCompat.getColorStateList(textView.context, R.color.red)
        textView.text = textView.resources.getString(R.string.row_car_status_exit)
    } else {
        textView.backgroundTintList =
            ContextCompat.getColorStateList(textView.context, R.color.materialGray5)
        textView.text = textView.resources.getString(R.string.row_car_status_parked)
    }
}