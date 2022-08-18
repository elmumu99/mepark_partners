package com.mrpark1.meparkpartner.ui.monthlyparking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.data.model.common.MonthParkedCar
import com.mrpark1.meparkpartner.util.CommandUtil
import com.mrpark1.meparkpartner.util.Constants

class MonthlyParkingDoneAdapter(): RecyclerView.Adapter<MonthlyParkingDoneAdapter.ViewHolder>() {

    private var monthlyParkingList = arrayListOf<MonthParkedCar>()
    fun setmonthlyParkingList(data: List<MonthParkedCar>){
        monthlyParkingList.clear()
        monthlyParkingList.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_monthly_parking_car, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(monthlyParkingList[position])
    }

    override fun getItemCount(): Int {
        return monthlyParkingList.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val tv_parking_lot_name = view.findViewById<TextView>(R.id.tv_parking_lot_name)
        val tv_d_day = view.findViewById<TextView>(R.id.tv_d_day)
        val tv_car_num = view.findViewById<TextView>(R.id.tv_car_num)
        val tv_car_type = view.findViewById<TextView>(R.id.tv_car_type)
        val iv_car_more = view.findViewById<ImageView>(R.id.iv_car_more)
        val tv_contact = view.findViewById<TextView>(R.id.tv_contact)
        val tv_period = view.findViewById<TextView>(R.id.tv_period)
        val bt_car_state = view.findViewById<Button>(R.id.bt_car_state)
        val tv_price = view.findViewById<TextView>(R.id.tv_price)

        fun bind(item: MonthParkedCar){
            tv_parking_lot_name.text = Constants.selectedParkingLot.Name
//            tv_d_day.text = d_day
            tv_car_num.text = item.LP
//            tv_car_type.text = 파라미터 없음
            tv_contact.text = item.Contact
            tv_period.text = item.StartDate + " ~ " + item.EndDate
            bt_car_state.text = "계약만료"
            tv_price.text = CommandUtil.addCommaNumInt(item.Profit)

            iv_car_more.visibility = View.GONE
        }
    }
}