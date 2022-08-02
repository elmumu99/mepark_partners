package com.mrpark1.meparkpartner.ui.hr

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.data.model.common.PartnerUser

class SettingPartnerUserAdapter(private val onItemClick:(Int,Int) -> Unit):
    RecyclerView.Adapter<SettingPartnerUserAdapter.ViewHolder>(){

    var partnerUserList = ArrayList<PartnerUser>()
    var isEditMode = false
    fun setPartnerUsers(list: List<PartnerUser>){
        partnerUserList.clear()
        partnerUserList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_partner_user_state, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(partnerUserList[position])

    }

    override fun getItemCount(): Int {
        return partnerUserList.size
    }

    fun setIsEditMode(mode: Boolean){
        isEditMode = mode
        notifyDataSetChanged()
    }



    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){

        private val tv_name = view.findViewById<TextView>(R.id.tv_name)
        private val view_arrow_right = view.findViewById<View>(R.id.view_arrow_right)
        private val tv_role = view.findViewById<TextView>(R.id.tv_role)
        private val tv_delete = view.findViewById<TextView>(R.id.tv_delete)


        fun bind(item: PartnerUser){
            tv_name.text = item.Name

            if(item.Role=="Administrator"){
                tv_role.text = "최초관리자"
            }else if(item.Role=="SubAdministrator"){
                tv_role.text = "관리자"
            }else{
                tv_role.text = "직원"
            }



            if(isEditMode){ //삭제버튼 활성화
                tv_delete.visibility = View.VISIBLE
                tv_role.visibility = View.GONE
                view_arrow_right.visibility = View.GONE
                tv_delete.setOnClickListener { onItemClick(adapterPosition,1) }
                view_arrow_right.setOnClickListener {}
            }else{ //삭제버튼 비활성화
                tv_delete.visibility = View.GONE
                tv_role.visibility = View.VISIBLE
                view_arrow_right.visibility = View.VISIBLE
                tv_delete.setOnClickListener {}
                view_arrow_right.setOnClickListener { onItemClick(adapterPosition,0) }
                tv_role.setOnClickListener { onItemClick(adapterPosition,0) }
            }
        }
    }



}
