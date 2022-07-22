package com.mrpark1.meparkpartner.ui.parkhistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mrpark1.meparkpartner.data.model.common.Car
import com.mrpark1.meparkpartner.databinding.RowCarBinding

class ParkHistoryAdapter(
    private val onItemClick: (Int) -> Unit,
    private val parkingLotName: String
) :
    ListAdapter<Car, RecyclerView.ViewHolder>(ParkHistoryDiffUtilCallback()) {

    class ParkHistoryDiffUtilCallback : DiffUtil.ItemCallback<Car>() {
        override fun areItemsTheSame(oldItem: Car, newItem: Car): Boolean =
            oldItem.CarID == newItem.CarID

        override fun areContentsTheSame(oldItem: Car, newItem: Car): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ParkHistoryHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ParkHistoryHolder).bind(getItem(position))
    }

    inner class ParkHistoryHolder(private val binding: RowCarBinding) :
        RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup) : this(
            RowCarBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        init {
            binding.btRowCarExit.setOnClickListener {
                onItemClick(adapterPosition)
            }
            binding.btRowCarExpand.setOnClickListener {
                currentList[adapterPosition].expanded = !currentList[adapterPosition].expanded
                notifyItemChanged(adapterPosition)
            }
        }

        fun bind(car: Car) {
            binding.car = car
            binding.parkingLotName = parkingLotName
            binding.isHistory = true
            binding.isRegular = car.VisitPlace == "일주차" || car.VisitPlace == "월주차"
        }
    }
}