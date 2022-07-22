package com.mrpark1.meparkpartner.ui.nopartner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.data.model.common.Invitation

class InviteAdapter : RecyclerView.Adapter<InviteAdapter.ViewHolder>(){

    var InvitationList = ArrayList<Invitation>()
    fun setInvitations(list: List<Invitation>){
        InvitationList.clear()
        InvitationList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_commuting_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(InvitationList[position])
    }

    override fun getItemCount(): Int {
        return InvitationList.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){

        private val tv_name = view.findViewById<TextView>(R.id.tv_name)
        private val tv_time = view.findViewById<TextView>(R.id.tv_time)
        private val tv_state = view.findViewById<TextView>(R.id.tv_state)


        fun bind(item: Invitation){

        }
    }


}