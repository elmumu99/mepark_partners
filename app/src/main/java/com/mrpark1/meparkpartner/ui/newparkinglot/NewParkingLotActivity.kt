package com.mrpark1.meparkpartner.ui.newparkinglot

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.data.model.common.ParkingLot
import com.mrpark1.meparkpartner.data.model.common.VisitPlace
import com.mrpark1.meparkpartner.databinding.ActivityNewParkingLotBinding
import com.mrpark1.meparkpartner.ui.Status
import com.mrpark1.meparkpartner.ui.common.BaseActivity
import com.mrpark1.meparkpartner.ui.dialogs.CommonDialog
import com.mrpark1.meparkpartner.ui.visitplace.VisitPlaceActivity
import com.mrpark1.meparkpartner.util.ImagePickerUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewParkingLotActivity :
    BaseActivity<ActivityNewParkingLotBinding>(R.layout.activity_new_parking_lot) {
    //주차장 추가 및 수정 액티비티 (다소 복잡한 로직)

    private val viewModel: NewParkingLotViewModel by viewModels()

    //이미지피커 콜백
    private val imagePickerResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { pickerResult ->
            viewModel.getImageFromPickerResult(pickerResult)
        }

    //방문지 액티비티 콜백
    private val visitPlaceResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            viewModel.updateVisitPlace(result)
        }

    //주소입력 액티비티 콜백
    private val addressResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            viewModel.updateAddress(result)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel

        //파트너스 최초 주차장 생성일 경우 뒤로가기 방지
        intent.getBooleanExtra("backPressEnabled", true).let {
            backPressEnabled = it
            if (!it) binding.tbNewParkingLot.navigationIcon = null
        }

        //주차장 정보 intent로 가져오기
        intent.getSerializableExtra("parkingLot")?.let {
            viewModel.initialize(it as ParkingLot)
        }

        viewModel.currentStatus.observe(this) {
            when (it) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> finish()
                Status.NEWPARK_ERROR_VISITPLACE -> {
                    Toast.makeText(this, "일부 방문지 반영에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                }
                Status.NEWPARK_ERROR_QR -> {
                    Toast.makeText(this, "주차장 QR 생성에 실패했습니다. 관리자에게 문의하세요.", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }
                Status.NEWPARK_PLACEHOLDER -> {
                    snackBar("수정 기능은 곧 추가됩니다!")
                }
                Status.ERROR_INTERNET -> snackBar(getString(R.string.common_error_internet))
                Status.ERROR_EXPIRED -> sessionExpired()
                Status.ERROR -> snackBar(getString(R.string.common_error_unknown))

                else -> {}
            }
            if (it != Status.LOADING) loadingDialog.cancel()
        }

        binding.tbNewParkingLot.setNavigationOnClickListener { finish() }

        //주차장 이미지 선택
        binding.ivNewParkingLotProfile.setOnClickListener {
            ImagePickerUtil.pickImage(this, imagePickerResult)
        }

        //방문지 설정
        binding.btNewParkingLotVisit.setOnClickListener { launchVisitPlace(null) }

        //최소 잔여면수 관련 도움말
        binding.btNewParkingLotHelpSpace.setOnClickListener {
            CommonDialog(
                this,
                message = "주차장의 잔여 면수가 ‘최소 잔여 면수’ 이하일 경우, 고객 앱의 검색에서 주차장이 노출되지 않습니다.\n\n" +
                        "월 주차 등으로 여유 주차 면수 확보가 필요한 경우 이 기능을 이용해 보세요. (미사용시 0으로 입력)"
            ).show()
        }

        //방문지 목록 RecyclerView
        binding.rvNewParkingLotVisit.layoutManager = LinearLayoutManager(this)
        binding.rvNewParkingLotVisit.adapter =
            NewParkingLotAdapter { launchVisitPlace(viewModel.visitPlaces[it]) }

        //주소 입력
        binding.etNewParkingLotAddress.setOnClickListener {
            addressResult.launch(
                Intent(this, AddressActivity::class.java)
            )
        }
    }

    private fun launchVisitPlace(editPlace: VisitPlace?) {
        visitPlaceResult.launch(Intent(this, VisitPlaceActivity::class.java).apply {
            putExtra("editPlace", editPlace)
        })
    }
}