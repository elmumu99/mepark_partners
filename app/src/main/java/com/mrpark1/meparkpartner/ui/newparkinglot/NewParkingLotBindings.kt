package com.mrpark1.meparkpartner.ui.newparkinglot

import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableArrayList
import androidx.recyclerview.widget.RecyclerView
import com.mrpark1.meparkpartner.data.model.common.VisitPlace

//RecyclerView를 위한 BindingAdapter
@BindingAdapter("bindNewParkingLotVisitPlaces")
fun bindNewParkingLotVisitPlaces(r: RecyclerView, places: ObservableArrayList<VisitPlace>?) {
    val adapter: NewParkingLotAdapter? = r.adapter as NewParkingLotAdapter?
    if (adapter == null || places == null) return
    adapter.submitList(places.toList())
}