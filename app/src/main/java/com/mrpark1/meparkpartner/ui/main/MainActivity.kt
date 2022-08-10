package com.mrpark1.meparkpartner.ui.main

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.databinding.ActivityMainBinding
import com.mrpark1.meparkpartner.ui.Status
import com.mrpark1.meparkpartner.ui.charge.ChargeActivity
import com.mrpark1.meparkpartner.ui.common.BaseActivity
import com.mrpark1.meparkpartner.ui.dialogs.CommonDialog
import com.mrpark1.meparkpartner.ui.dialogs.spinner.BottomSheetSpinner
import com.mrpark1.meparkpartner.ui.enter.EnterActivity
import com.mrpark1.meparkpartner.ui.hr.HrActivity
import com.mrpark1.meparkpartner.ui.managesale.ManageSaleActivity
import com.mrpark1.meparkpartner.ui.newparkinglot.NewParkingLotActivity
import com.mrpark1.meparkpartner.ui.nopartner.NoPartnerActivity
import com.mrpark1.meparkpartner.ui.park.ParkActivity
import com.mrpark1.meparkpartner.ui.parkhistory.ParkHistoryActivity
import com.mrpark1.meparkpartner.ui.parkinglotsetting.ParkingLotSettingActivity
import com.mrpark1.meparkpartner.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//Activity 상단에는 @AndroidEntryPoint 어노테이션을 달아줘야 Hilt 주입이 가능함. (가능하면 모든 액티비티에)
@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    //ViewModel 초기화 (이때 Hilt가 사용됨)
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Databinding의 viewModel 변수에 초기화한 viewModel 전달
        binding.viewModel = viewModel

        //ViewModel 변경사항 감지 및 동작 수행
        viewModel.currentStatus.observe(this) {
            when (it) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> {}
                Status.MAIN_NO_PARKING_LOTS -> dialogNewParkingLot()
                Status.MAIN_NO_PARTNER -> dialogNoPartner()
                Status.ERROR_INTERNET -> snackBar(getString(R.string.common_error_internet))
                Status.ERROR_EXPIRED -> sessionExpired()
                Status.ERROR -> snackBar(getString(R.string.common_error_unknown))
                else -> {}
            }
            if (it != Status.LOADING) loadingDialog.cancel()
        }

        viewModel.commutingStatus.observe(this){
            when(it){
                "2" ->{
                    binding.btMainQr.text = getString(R.string.main_text_qr_finish)
                }
                else ->{
                    binding.btMainQr.text = getString(R.string.main_text_qr_start)
                }
            }
        }

        //주차장 선택 다이얼로그
        binding.btMainSelectParkinglot.setOnClickListener {
            BottomSheetSpinner().setInfo(
                getString(R.string.main_bottomsheet_select_parkinglot_title),
                viewModel.parkingLots.map { it.Name },
                onItemClick = {
                    viewModel.setCurrentParkingLot(viewModel.parkingLots[it])
                }).run { show(supportFragmentManager, tag) }
        }

        //새로고침 버튼
        binding.btMainRefresh.setOnClickListener { viewModel.getMyParkingLots() }

        //입차관리
        binding.btMainEnter.setOnClickListener {
            startActivity(Intent(this, EnterActivity::class.java).apply {
                putExtra("parkingLot", viewModel.selectedParkingLot.value)
            })
        }

        //주차관리
        binding.btMainPark.setOnClickListener {
            startActivity(Intent(this, ParkActivity::class.java).apply {
                putExtra("parkingLot", viewModel.selectedParkingLot.value)
            })
        }

        //출차내역
        binding.btMainParkHistory.setOnClickListener {
            startActivity(Intent(this, ParkHistoryActivity::class.java).apply {
                putExtra("parkingLot", viewModel.selectedParkingLot.value)
            })
        }

        //매출관리
        binding.btMainProfit.setOnClickListener {
            if(Constants.isAdmin){
//                snackBar(R.string.common_placeholder)
                startActivity(Intent(this, ManageSaleActivity::class.java))

            }else{
                snackBar(R.string.main_need_admin)
            }
        }

        //주차장 설정
        binding.btMainParkinglotSetting.setOnClickListener {
            if(Constants.isAdmin){
                startActivity(Intent(this, ParkingLotSettingActivity::class.java).apply {
                    putExtra("parkingLot", viewModel.selectedParkingLot.value)
                })
            }else{
                snackBar(R.string.main_need_admin)
            }
        }

        //인사관리
        binding.btMainHr.setOnClickListener {
            if(Constants.isAdmin){
                startActivity(Intent(this, HrActivity::class.java).apply {
                    putExtra("parkingLot", viewModel.selectedParkingLot.value)
                })
            }else{
                snackBar(R.string.main_need_admin)
            }

        }

        //정산
        binding.btMainCharge.setOnClickListener {
            if(Constants.isAdmin){
                snackBar(R.string.common_placeholder)
            }else{
                snackBar(R.string.main_need_admin)
            }

//            startActivity(Intent(this, InsuranceActivity::class.java).apply {
//                putExtra("currentPlan", viewModel.selectedParkingLot.value!!.Insurance ?: "N")
//            })
        }

        //보험
        binding.btMainInsurance.setOnClickListener {
            if(Constants.isAdmin){
                startActivity(Intent(this, ChargeActivity::class.java).apply {
                    putExtra("parkingLot", viewModel.selectedParkingLot.value)
                })
            }else{
                snackBar(R.string.main_need_admin)
            }
        }

        //QR 출퇴근
        //https://www.the-qrcode-generator.com/ 테스트 qr 생성 가능
        binding.btMainQr.setOnClickListener {
            barcodeLauncher.launch(ScanOptions().setOrientationLocked(true))
        }

        binding.tvMainName.setOnClickListener {
            val list = resources.getStringArray(R.array.bank_names)
            BottomSheetSpinner().setInfo(
                getString(R.string.new_partner_bottomsheet_title),
                list.asList(),
                onItemClick = {
                    snackBar(list[it])
                },
                desc = getString(R.string.new_partner_bottomsheet_desc)
            ).run { show(supportFragmentManager, tag) }
        }
    }

    //QR 스캐너 콜백
    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        result.contents?.let {
            if (it.length == 6) viewModel.updateCommuting(it)
            else snackBar("유효하지 않은 QR입니다.")
        }
    }

    //다른 화면 갔다 올때마다 새로고침
    override fun onResume() {
        super.onResume()
        viewModel.getMyParkingLots()
    }

    //파트너사 최초 생성 시 주차장 1개 의무 생성
    private fun dialogNewParkingLot() = CommonDialog(this,
        title = getString(R.string.main_dialog_new_parkinglot_title),
        message = getString(R.string.main_dialog_new_parkinglot_message),
        cancelable = false,
        onPositive = {
            startActivity(Intent(this, NewParkingLotActivity::class.java)
                .apply { putExtra("backPressEnabled", false) })
        }).show()

    //파트너 해고 등
    private fun dialogNoPartner() = CommonDialog(this,
        title = getString(R.string.main_dialog_no_partner_title),
        message = getString(R.string.main_dialog_no_partner_message),
        cancelable = false,
        onPositive = {
            startActivity(Intent(this, NoPartnerActivity::class.java))
            finish()
        }).show()

    //뒤로가기 활성화/비활성화
    private var backPressedOnce = false

    override fun onBackPressed() {
        when {
            backPressedOnce -> finishAffinity()
            else -> {
                Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT)
                    .show()
                backPressedOnce = true
                GlobalScope.launch {
                    delay(3000)
                    backPressedOnce = false
                }
            }
        }
    }
}