package com.mrpark1.meparkpartner.ui.parkinglotsetting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mrpark1.meparkpartner.data.model.common.ParkingLot
import com.mrpark1.meparkpartner.databinding.RowParkinglotBinding

class ParkingLotSettingAdapter(
    private val onItemClick: (ParkingLot) -> Unit,
) :
    ListAdapter<ParkingLot, RecyclerView.ViewHolder>(ParkingLotSettingDiffUtilCallback()) {

    class ParkingLotSettingDiffUtilCallback : DiffUtil.ItemCallback<ParkingLot>() {
        override fun areItemsTheSame(oldItem: ParkingLot, newItem: ParkingLot): Boolean =
            oldItem.ParkingLN == newItem.ParkingLN

        override fun areContentsTheSame(oldItem: ParkingLot, newItem: ParkingLot): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ParkingLotSettingHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ParkingLotSettingHolder).bind(getItem(position))
    }

    inner class ParkingLotSettingHolder(private val binding: RowParkinglotBinding) :
        RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup) : this(
            RowParkinglotBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        init {
            binding.root.setOnClickListener {
                onItemClick(currentList[adapterPosition])
            }
        }

        fun bind(parkingLot: ParkingLot) {
            binding.parkingLot = parkingLot
        }
    }
}