package com.mrpark1.meparkpartner.ui.hr

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.data.model.common.PartnerUser

class PartnerUserAdapter():
    RecyclerView.Adapter<PartnerUserAdapter.ViewHolder>(){

    var partnerUserList = ArrayList<PartnerUser>()
    fun setPartnerUsers(list: List<PartnerUser>){
        partnerUserList.clear()
        partnerUserList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_commuting_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(partnerUserList[position])
    }

    override fun getItemCount(): Int {
        return partnerUserList.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){

        private val tv_name = view.findViewById<TextView>(R.id.tv_name)
        private val tv_time = view.findViewById<TextView>(R.id.tv_time)
        private val tv_state = view.findViewById<TextView>(R.id.tv_state)


        fun bind(item: PartnerUser){
            tv_name.text = item.Name
            when(item.CommutingStatus){
                "1" ->{ //근무한적 없음
                    tv_state.text = "첫출근"
                    tv_time.text = "근무한적 없음"
                }
                "2" ->{ //출근
                    tv_state.text = "출근"
                    tv_time.text = item.StartDateTime
                }
                "3" ->{ //퇴근
                    tv_state.text = "퇴근"
                    tv_time.text = item.EndTime
                }
            }


        }
    }


}
