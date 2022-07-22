package com.mrpark1.meparkpartner.ui.charge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.data.model.payment.CardPaymentInfo

class ChargingHistoryAdapter():
    RecyclerView.Adapter<ChargingHistoryAdapter.ViewHolder>(){

    var chargingHistoryList = arrayListOf<CardPaymentInfo>()
    fun setChargingHistoryList(data: List<CardPaymentInfo>){
        chargingHistoryList.clear()
        chargingHistoryList.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_charging_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(chargingHistoryList[position])
    }

    override fun getItemCount(): Int {
        return chargingHistoryList.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){

        private val tv_payment_title = view.findViewById<TextView>(R.id.tv_payment_title)
        private val tv_payment_type = view.findViewById<TextView>(R.id.tv_payment_type)

        private val tv_payment_date = view.findViewById<TextView>(R.id.tv_payment_date)
        private val tv_payment_amt = view.findViewById<TextView>(R.id.tv_payment_amt)
        private val tv_payment_auth_date = view.findViewById<TextView>(R.id.tv_payment_auth_date)

        private val tv_payment_card = view.findViewById<TextView>(R.id.tv_payment_card)
        private val tv_payment_card_num = view.findViewById<TextView>(R.id.tv_payment_card_num)


        fun bind(item: CardPaymentInfo){
            tv_payment_title.text = item.P_FN_NM
            if(item.P_STATUS == "02"){ //가상계좌
                tv_payment_type.text = "무통장입금"
                tv_payment_card.visibility = View.GONE
                tv_payment_card_num.visibility = View.GONE
            }else{ //카드
                tv_payment_type.text = "카드"
                tv_payment_card_num.text = item.P_CARD_NUM
            }

            tv_payment_date.text = item.P_AUTH_DT
            tv_payment_amt.text = item.P_AMT
            tv_payment_auth_date.text = item.P_AUTH_DT
        }
    }
}