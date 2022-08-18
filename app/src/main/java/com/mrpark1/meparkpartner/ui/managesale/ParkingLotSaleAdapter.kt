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
import com.mrpark1.meparkpartner.util.CommandUtil

class ParkingLotSaleAdapter(private val onItemClick:(Int) -> Unit):
    RecyclerView.Adapter<ParkingLotSaleAdapter.ViewHolder>() {
    var visitPlaceList = ArrayList<VisitPlaceData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_parking_lot_sale, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(visitPlaceList[position])
    }

    override fun getItemCount(): Int {
        return visitPlaceList.size
    }

    fun setListItem(list: ArrayList<VisitPlaceData>){
        visitPlaceList.clear()
        visitPlaceList.addAll(list)
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private val tv_visit_place_name = view.findViewById<TextView>(R.id.tv_visit_place_name)
        private val tv_sale = view.findViewById<TextView>(R.id.tv_sale)
        private val explainLayout = view.findViewById<ConstraintLayout>(R.id.explainLayout)

        fun bind(item: VisitPlaceData){
            tv_visit_place_name.text = item.VisitPlace
            tv_sale.text = CommandUtil.addCommaNumInt(item.VisitPlaceProfit.toString())
            explainLayout.setOnClickListener { onItemClick(adapterPosition)}
        }

    }

}