package com.mrpark1.meparkpartner.ui.dialogs.spinner

import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableArrayList
import androidx.recyclerview.widget.RecyclerView

//RecyclerView를 위한 BindingAdapter
@BindingAdapter("bindBottomSheetSpinnerItems")
fun bindBottomSheetSpinnerItems(r: RecyclerView, items: ObservableArrayList<String>?) {
    val adapter: BottomSheetSpinnerAdapter? = r.adapter as BottomSheetSpinnerAdapter?
    if (adapter == null || items == null) return
    adapter.submitList(items.toList())
}