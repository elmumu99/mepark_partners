package com.mrpark1.meparkpartner.ui.dialogs.spinner

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableArrayList
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mrpark1.meparkpartner.databinding.RowBottomsheetSpinnerBinding
import com.mrpark1.meparkpartner.util.Constants

class BottomSheetSpinnerAdapter(private val onItemClick: (Int) -> Unit) :
    ListAdapter<String, RecyclerView.ViewHolder>(BottomSheetSpinnerDiffUtilCallback()) {

    class BottomSheetSpinnerDiffUtilCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem.hashCode() == newItem.hashCode()

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BottomSheetSpinnerHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as BottomSheetSpinnerHolder).bind(getItem(position))

        if(position<Constants.parkingLots.size){
            if(getItem(position) == Constants.parkingLots[position].Name){
                (holder as BottomSheetSpinnerHolder).bindCount(Constants.parkingLots[position].ParkedCarCount.toString())
            }
        }
    }

    inner class BottomSheetSpinnerHolder(private val binding: RowBottomsheetSpinnerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup) : this(
            RowBottomsheetSpinnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

        init {
            binding.root.setOnClickListener {
                onItemClick(adapterPosition)
            }
        }

        fun bind(text: String) {
            binding.text = text
        }

        fun bindCount(count: String){
            binding.count = count
        }
    }
}