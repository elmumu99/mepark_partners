package com.mrpark1.meparkpartner.ui.park

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mrpark1.meparkpartner.data.model.common.Car
import com.mrpark1.meparkpartner.databinding.RowCarBinding

class ParkAdapter(
    private val onExitClick: (Car) -> Unit, //출차 콜백
    private val onMoreClick: (Car) -> Unit, //더보기 콜백
    private val parkingLotName: String
) :
    ListAdapter<Car, RecyclerView.ViewHolder>(ParkDiffUtilCallback()) {

    //DiffUtil (리스트 갱신 기능)
    class ParkDiffUtilCallback : DiffUtil.ItemCallback<Car>() {
        override fun areItemsTheSame(oldItem: Car, newItem: Car): Boolean =
            oldItem.CarID == newItem.CarID

        override fun areContentsTheSame(oldItem: Car, newItem: Car): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ParkHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ParkHolder).bind(getItem(position))
    }

    //Holder class
    inner class ParkHolder(private val binding: RowCarBinding) :
        RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup) : this(
            RowCarBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        //버튼 클릭 이벤트 설정
        init {
            binding.btRowCarExit.setOnClickListener {
                onExitClick(currentList[adapterPosition])
            }
            binding.btRowCarMore.setOnClickListener {
                onMoreClick(currentList[adapterPosition])
            }
            binding.btRowCarExpand.setOnClickListener {
                currentList[adapterPosition].expanded = !currentList[adapterPosition].expanded
                notifyItemChanged(adapterPosition)
            }
        }

        fun bind(car: Car) {
            binding.car = car
            binding.parkingLotName = parkingLotName
            binding.isHistory = false
            binding.isRegular = car.VisitPlace == "일주차" || car.VisitPlace == "월주차"
        }
    }
}