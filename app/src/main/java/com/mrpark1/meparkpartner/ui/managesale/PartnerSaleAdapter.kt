package com.mrpark1.meparkpartner.ui.managesale

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.data.model.managesale.ParkingLotData
import com.mrpark1.meparkpartner.data.model.managesale.VisitPlaceData
import com.mrpark1.meparkpartner.ui.hr.SettingPartnerUserAdapter

class PartnerSaleAdapter(private val onItemClick:(Int) -> Unit):
    RecyclerView.Adapter<PartnerSaleAdapter.ViewHolder>() {
    var parkingLotDataList = ArrayList<ParkingLotData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_partner_sale, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(parkingLotDataList[position])
    }

    override fun getItemCount(): Int {
        return parkingLotDataList.size
    }

    fun setListItem(list: ArrayList<ParkingLotData>){
        parkingLotDataList.clear()
        parkingLotDataList.addAll(list)
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private val tv_parking_lot_name = view.findViewById<TextView>(R.id.tv_parking_lot_name)
        private val tv_sale = view.findViewById<TextView>(R.id.tv_sale)
        private val explainLayout = view.findViewById<ConstraintLayout>(R.id.explainLayout)

        fun bind(item: ParkingLotData){
            tv_parking_lot_name.text = item.Name
            tv_sale.text = item.ParkingLotProfit.toString()
            explainLayout.setOnClickListener { onItemClick(adapterPosition)}
        }

    }

}