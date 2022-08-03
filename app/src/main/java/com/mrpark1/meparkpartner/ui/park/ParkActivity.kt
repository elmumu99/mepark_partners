package com.mrpark1.meparkpartner.ui.park

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.data.model.common.Car
import com.mrpark1.meparkpartner.data.model.common.ParkingLot
import com.mrpark1.meparkpartner.databinding.ActivityParkBinding
import com.mrpark1.meparkpartner.ui.Status
import com.mrpark1.meparkpartner.ui.car.CarMoreActivity
import com.mrpark1.meparkpartner.ui.common.BaseActivity
import com.mrpark1.meparkpartner.ui.dialogs.CommonDialog
import com.mrpark1.meparkpartner.ui.enter.EnterActivity
import com.mrpark1.meparkpartner.ui.more.MoreActivity
import com.mrpark1.meparkpartner.ui.parkhistory.ParkHistoryActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ParkActivity : BaseActivity<ActivityParkBinding>(R.layout.activity_park) {
    //주차관리 액티비티

    private val viewModel: ParkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        val parkingLot = intent.getSerializableExtra("parkingLot") as ParkingLot
        viewModel.parkingLot = parkingLot

        viewModel.currentStatus.observe(this) {
            when (it) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> {}
                Status.PARK_EXIT_SUCCESS -> snackBar("정상적으로 출차되었습니다!")
                Status.ERROR_INTERNET -> snackBar(getString(R.string.common_error_internet))
                Status.ERROR_EXPIRED -> sessionExpired()
                Status.ERROR -> {
                    if(viewModel.errorMessage==""){
                        snackBar(getString(R.string.common_error_unknown))
                    } else {
                        snackBar(viewModel.errorMessage)
                    }
                }
                else -> {}
            }
            if (it != Status.LOADING) loadingDialog.cancel()
        }

        //차량 목록 RecyclerView
        binding.rv.layoutManager = LinearLayoutManager(this)
        binding.rv.adapter =
            ParkAdapter(
                { exitCarDialog(it.CarID) },
                { startMoreActivity(it, parkingLot) },
                parkingLot.Name
            )

        //새로고침
        binding.tbPark.setOnMenuItemClickListener {
            if (it.itemId == R.id.menu_park_refresh) viewModel.getParkedCars()
            false
        }

        binding.tbPark.setNavigationOnClickListener { finish() }

        //바텀내비
        binding.bnPark.selectedItemId = R.id.bottom_nav_park
        binding.bnPark.setOnItemSelectedListener {
            if (it.itemId == R.id.bottom_nav_park) return@setOnItemSelectedListener false
            startActivity(Intent(
                this, when (it.itemId) {
                    R.id.bottom_nav_enter -> EnterActivity::class.java
                    R.id.bottom_nav_park_history -> ParkHistoryActivity::class.java
                    else -> MoreActivity::class.java
                }
            ).apply {
                putExtra("parkingLot", viewModel.parkingLot)
            })
            finish()
            false
        }
    }

    //출차 다이얼로그
    private fun exitCarDialog(carId: String) = CommonDialog(this,
        title = "출차하기",
        message = "이 차량을 출차 완료 처리할까요?",
        cancelable = true,
        onPositive = { viewModel.exitCar(carId) }).show()

    //차량 더보기 액티비티
    private fun startMoreActivity(car: Car, parkingLot: ParkingLot) {
        startActivity(Intent(this, CarMoreActivity::class.java).apply {
            putExtra("car", car)
            putExtra("parkingLot", parkingLot)
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.getParkedCars()
    }
}