package com.mrpark1.meparkpartner.ui.newparkinglot

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mrpark1.meparkpartner.data.model.common.VisitPlace
import com.mrpark1.meparkpartner.databinding.RowParkinglotVisitplaceBinding

class NewParkingLotAdapter(private val onItemClick: (Int) -> Unit) :
    ListAdapter<VisitPlace, RecyclerView.ViewHolder>(NewParkingLotDiffUtilCallback()) {

    class NewParkingLotDiffUtilCallback : DiffUtil.ItemCallback<VisitPlace>() {
        override fun areItemsTheSame(oldItem: VisitPlace, newItem: VisitPlace): Boolean =
            oldItem.PlaceName == newItem.PlaceName

        override fun areContentsTheSame(oldItem: VisitPlace, newItem: VisitPlace): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NewParkingLotHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as NewParkingLotHolder).bind(getItem(position))
    }

    inner class NewParkingLotHolder(private val binding: RowParkinglotVisitplaceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup) : this(
            RowParkinglotVisitplaceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        init {
            binding.root.setOnClickListener {
                onItemClick(adapterPosition)
            }
        }

        fun bind(visitPlace: VisitPlace) {
            binding.text = visitPlace.PlaceName
        }
    }


}