package com.mrpark1.meparkpartner.ui.insurance

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.databinding.ActivityInsuranceBinding
import com.mrpark1.meparkpartner.ui.common.BaseActivity
import com.mrpark1.meparkpartner.ui.newparkinglot.NewParkingLotActivity

class InsuranceActivity : BaseActivity<ActivityInsuranceBinding>(R.layout.activity_insurance) {
    //보험 액티비티 (현재 미사용)

    private val viewModel: InsuranceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        viewModel.initialize(intent.getStringExtra("currentPlan"))

        binding.btInsuranceApply.setOnClickListener {
            setResult(
                RESULT_OK,
                Intent(this, NewParkingLotActivity::class.java).apply {
                    putExtra("selectedPlan", viewModel.selectedPlan.value)
                })
            finish()
        }

        binding.tbInsurance.setNavigationOnClickListener { finish() }
    }
}