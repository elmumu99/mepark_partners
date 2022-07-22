package com.mrpark1.meparkpartner.ui.charge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.data.model.payment.PointReceipt

class PointHistoryAdapter():
    RecyclerView.Adapter<PointHistoryAdapter.ViewHolder>(){

    var pointHistoryList = arrayListOf<PointReceipt>()
    fun setpointHistoryList(data: List<PointReceipt>){
        pointHistoryList.clear()
        pointHistoryList.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_point_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(pointHistoryList[position])
    }

    override fun getItemCount(): Int {
        return pointHistoryList.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val tv_parking_lot_name = view.findViewById<TextView>(R.id.tv_parking_lot_name)
        val tv_insurance = view.findViewById<TextView>(R.id.tv_insurance)
        val tv_car_num = view.findViewById<TextView>(R.id.tv_car_num)
        val tv_car_type = view.findViewById<TextView>(R.id.tv_car_type)
        val tv_enter_time = view.findViewById<TextView>(R.id.tv_enter_time)
        val tv_total_time = view.findViewById<TextView>(R.id.tv_total_time)

        fun bind(item: PointReceipt){
            tv_parking_lot_name.text = item.Name
            when(item.Insurance){ // 보험 종류 A: 일반형, B: 렌트형, N:보험미사용
                "A" ->{tv_insurance.text = "일반형"}
                "B" ->{tv_insurance.text = "렌트형"}
                "N" ->{tv_insurance.text = "보험미사용"}
            }
            tv_car_num.text = item.LP
            var carType = ""
            if(item.CarType == "Small"){
                carType = "(소형)"
            }else{
                carType = "(대형/SUV)"
            }
            tv_car_type.text = carType
            tv_enter_time.text = item.EnterDateTime
            var totalTime = ""
            var hTime = ""
            var mTime = ""
            try{
                hTime = (item.TotalTime.toInt()/60).toString()
                if(hTime.length<2){
                    hTime = "0"+hTime
                }

                mTime = (item.TotalTime.toInt()%60).toString()
                if(mTime.length<2){
                    mTime = "0"+mTime
                }

                totalTime = "$hTime:$mTime"
            }catch (e: Exception){
                totalTime = "00:00"
            }
            tv_total_time.text = totalTime
        }
    }
}