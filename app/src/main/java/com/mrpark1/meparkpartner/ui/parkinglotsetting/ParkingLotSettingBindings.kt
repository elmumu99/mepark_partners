package com.mrpark1.meparkpartner.ui.parkinglotsetting

import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableArrayList
import androidx.recyclerview.widget.RecyclerView
import com.mrpark1.meparkpartner.data.model.common.ParkingLot

@BindingAdapter("bindParkingLotSettingParkingLots")
fun bindParkingLotSettingParkingLots(
    r: RecyclerView,
    parkingLots: ObservableArrayList<ParkingLot>?
) {
    val adapter: ParkingLotSettingAdapter? = r.adapter as ParkingLotSettingAdapter?
    if (adapter == null || parkingLots == null) return
    adapter.submitList(parkingLots.toList())
}