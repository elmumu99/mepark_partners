package com.mrpark1.meparkpartner.ui.enter

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.data.model.common.ParkingLot
import com.mrpark1.meparkpartner.databinding.ActivityEnterBinding
import com.mrpark1.meparkpartner.ui.Status
import com.mrpark1.meparkpartner.ui.common.BaseActivity
import com.mrpark1.meparkpartner.ui.dialogs.spinner.BottomSheetSpinner
import com.mrpark1.meparkpartner.ui.more.MoreActivity
import com.mrpark1.meparkpartner.ui.park.ParkActivity
import com.mrpark1.meparkpartner.ui.parkhistory.ParkHistoryActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnterActivity : BaseActivity<ActivityEnterBinding>(R.layout.activity_enter) {
    //입차관리 액티비티

    private val viewModel: EnterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel

        //주차장 정보 intent 통해 불러옴
        viewModel.parkingLot = intent.getSerializableExtra("parkingLot") as ParkingLot

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

        //방문지 선택
        binding.btEnterVisitplaceSpinner.setOnClickListener {
            val list = viewModel.parkingLot.VisitPlaces.filter { it.DefaultFee.isNotBlank() }
                .map { it.PlaceName }
            BottomSheetSpinner().setInfo(
                getString(R.string.enter_bottomsheet_title_visitplace),
                list,
                onItemClick = {
                    viewModel.visitPlace.value = list[it]
                }
            ).run { show(supportFragmentManager, tag) }
        }

        binding.btEnterTabReserve.setOnClickListener { snackBar(getString(R.string.enter_reserve_placeholder)) }

        binding.tbEnter.setNavigationOnClickListener { finish() }

        //바텀내비처럼 생긴 무언가 구현
        binding.bnEnter.selectedItemId = R.id.bottom_nav_enter
        binding.bnEnter.setOnItemSelectedListener {
            if (it.itemId == R.id.bottom_nav_enter) return@setOnItemSelectedListener false
            startActivity(Intent(
                this, when (it.itemId) {
                    R.id.bottom_nav_park -> ParkActivity::class.java
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
}