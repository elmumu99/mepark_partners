package com.mrpark1.meparkpartner.ui.parkhistory

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.data.model.common.ParkingLot
import com.mrpark1.meparkpartner.databinding.ActivityParkHistoryBinding
import com.mrpark1.meparkpartner.ui.Status
import com.mrpark1.meparkpartner.ui.common.BaseActivity
import com.mrpark1.meparkpartner.ui.enter.EnterActivity
import com.mrpark1.meparkpartner.ui.more.MoreActivity
import com.mrpark1.meparkpartner.ui.park.ParkActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ParkHistoryActivity :
    BaseActivity<ActivityParkHistoryBinding>(R.layout.activity_park_history) {
    //출차내역 액티비티

    private val viewModel: ParkHistoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        val parkingLot = intent.getSerializableExtra("parkingLot") as ParkingLot
        viewModel.parkingLot = parkingLot

        viewModel.currentStatus.observe(this) {
            when (it) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> {}
                Status.PARKHIS_NO_CARS -> snackBar("해당 날짜에 입차한 차량이 없습니다.")
                Status.ERROR_INTERNET -> snackBar(getString(R.string.common_error_internet))
                Status.ERROR_EXPIRED -> sessionExpired()
                Status.ERROR -> snackBar(getString(R.string.common_error_unknown))
                else -> {}
            }
            if (it != Status.LOADING) loadingDialog.cancel()
        }

        binding.rv.layoutManager = LinearLayoutManager(this)
        binding.rv.adapter = ParkHistoryAdapter({}, parkingLot.Name)

        //날짜 선택 다이얼로그
        binding.editText.setOnClickListener {
            DatePickerDialog(
                this,
                R.style.DatePickerDialogTheme,
                { _, year, month, day ->
                    viewModel.setSelectedDate(
                        "$year-${String.format("%02d", month + 1)}-" +
                                String.format("%02d", day)
                    )
                },
                viewModel.selectedDate.value!!.substring(0, 4).toInt(),
                viewModel.selectedDate.value!!.substring(5, 7).toInt() - 1,
                viewModel.selectedDate.value!!.substring(8, 10).toInt()
            ).show()
        }

        binding.tbPark.setNavigationOnClickListener { finish() }

        //바텀내비
        binding.bnParkHistory.selectedItemId = R.id.bottom_nav_park_history
        binding.bnParkHistory.setOnItemSelectedListener {
            if (it.itemId == R.id.bottom_nav_park_history) return@setOnItemSelectedListener false
            startActivity(
                Intent(
                this, when (it.itemId) {
                    R.id.bottom_nav_enter -> EnterActivity::class.java
                    R.id.bottom_nav_park -> ParkActivity::class.java
                    else -> MoreActivity::class.java
                }
            ).apply {
                putExtra("parkingLot", viewModel.parkingLot)
            })
            finish()
            false
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getParkHistory()
    }
}