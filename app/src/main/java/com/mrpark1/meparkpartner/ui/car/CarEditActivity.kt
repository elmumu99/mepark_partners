package com.mrpark1.meparkpartner.ui.car

import android.app.Activity
import android.os.Bundle
import androidx.activity.viewModels
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.data.model.common.Car
import com.mrpark1.meparkpartner.data.model.common.ParkingLot
import com.mrpark1.meparkpartner.databinding.ActivityCarEditBinding
import com.mrpark1.meparkpartner.ui.Status
import com.mrpark1.meparkpartner.ui.common.BaseActivity
import com.mrpark1.meparkpartner.ui.dialogs.spinner.BottomSheetSpinner
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CarEditActivity : BaseActivity<ActivityCarEditBinding>(R.layout.activity_car_edit) {
    //차량 정보 수정 액티비티입니다.

    private val viewModel: CarEditViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel

        //차량 및 주차장 정보 intent로 받아옴
        val car = intent.getSerializableExtra("car") as Car
        viewModel.initialize(car)
        val parkingLot = intent.getSerializableExtra("parkingLot") as ParkingLot

        viewModel.currentStatus.observe(this) {
            when (it) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                Status.ERROR_INTERNET -> snackBar(getString(R.string.common_error_internet))
                Status.ERROR_EXPIRED -> sessionExpired()
                Status.ERROR -> snackBar(getString(R.string.common_error_unknown))
                else -> {}
            }
            if (it != Status.LOADING) loadingDialog.cancel()
        }

        //방문지 수정 시 선택지 다이얼로그
        binding.btCarEditVisitplaceSpinner.setOnClickListener {
            val list =
                parkingLot.VisitPlaces.filter { it.PlaceName != "일주차" && it.PlaceName != "월주차" }
                    .map { it.PlaceName }
            BottomSheetSpinner().setInfo(
                getString(R.string.enter_bottomsheet_title_visitplace),
                list,
                onItemClick = {
                    viewModel.visitPlace.value = list[it]
                }
            ).run { show(supportFragmentManager, tag) }
        }

        binding.tbCarEdit.setNavigationOnClickListener { finish() }
    }
}