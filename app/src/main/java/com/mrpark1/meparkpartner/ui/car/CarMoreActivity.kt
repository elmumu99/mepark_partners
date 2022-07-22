package com.mrpark1.meparkpartner.ui.car

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.data.model.common.Car
import com.mrpark1.meparkpartner.data.model.common.ParkingLot
import com.mrpark1.meparkpartner.databinding.ActivityCarMoreBinding
import com.mrpark1.meparkpartner.ui.Status
import com.mrpark1.meparkpartner.ui.common.BaseActivity
import com.mrpark1.meparkpartner.ui.dialogs.CommonDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CarMoreActivity : BaseActivity<ActivityCarMoreBinding>(R.layout.activity_car_more) {
    //차량 정보 더보기 액티비티입니다. 회차 및 할인/추가금액 설정 기능을 제공합니다.

    private val viewModel: CarMoreViewModel by viewModels()

    //차량 수정 액티비티 콜백
    private val editResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { if (it.resultCode == Activity.RESULT_OK) finish() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel

        //차량 정보 intent로 받아옴
        val car = intent.getSerializableExtra("car") as Car
        viewModel.initialize(car)

        binding.tbCarMore.setNavigationOnClickListener { finish() }

        //수정 버튼 클릭 시
        binding.btCarMoreEdit.setOnClickListener {
            editResult.launch(Intent(this, CarEditActivity::class.java).apply {
                putExtra("car", car)
                putExtra("parkingLot", intent.getSerializableExtra("parkingLot") as ParkingLot)
            })
        }

        //회차 처리
        binding.btCarMoreReturn.setOnClickListener {
            CommonDialog(this,
                title = "회차하기",
                message = "이 차량을 회차 처리할까요?",
                cancelable = true,
                onPositive = { viewModel.returnCar() }).show()
        }

        viewModel.currentStatus.observe(this) {
            when (it) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> finish()
                Status.ERROR_INTERNET -> snackBar(getString(R.string.common_error_internet))
                Status.ERROR_EXPIRED -> sessionExpired()
                Status.ERROR -> snackBar(getString(R.string.common_error_unknown))
                else -> {}
            }
            if (it != Status.LOADING) loadingDialog.cancel()
        }
    }
}