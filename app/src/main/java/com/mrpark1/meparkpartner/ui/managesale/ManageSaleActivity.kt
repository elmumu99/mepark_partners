package com.mrpark1.meparkpartner.ui.managesale

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayout
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.databinding.ActivityManageSaleBinding
import com.mrpark1.meparkpartner.ui.common.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageSaleActivity : BaseActivity<ActivityManageSaleBinding>(R.layout.activity_manage_sale) {

    private val parkingLotFragment = ManageSaleParkingLotFragment()
    private val corporationsFragment = ManageSaleCorporationsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.tbManageSale.setNavigationOnClickListener { finish() }


        supportFragmentManager.beginTransaction().add(R.id.fl_frag_container,parkingLotFragment).commit()


        binding.tlManageSaleTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab?.position

                if(position==0){
                    supportFragmentManager.beginTransaction().replace(R.id.fl_frag_container,parkingLotFragment).commit()
                }else{
                    supportFragmentManager.beginTransaction().replace(R.id.fl_frag_container,corporationsFragment).commit()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
//        binding.tiParkingLot.setOnClickListener {
//            supportFragmentManager.beginTransaction().replace(R.id.fl_frag_container,parkingLotFragment)
//        }
//
//        binding.tiCorporations.setOnClickListener {
//            supportFragmentManager.beginTransaction().replace(R.id.fl_frag_container,corporationsFragment)
//        }
    }
}