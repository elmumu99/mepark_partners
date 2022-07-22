package com.mrpark1.meparkpartner.ui.visitplace

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.data.model.common.VisitPlace
import com.mrpark1.meparkpartner.databinding.ActivityVisitPlaceBinding
import com.mrpark1.meparkpartner.ui.common.BaseActivity
import com.mrpark1.meparkpartner.ui.dialogs.CommonDialog
import com.mrpark1.meparkpartner.ui.newparkinglot.NewParkingLotActivity

class VisitPlaceActivity : BaseActivity<ActivityVisitPlaceBinding>(R.layout.activity_visit_place) {
    //방문지 수정/추가 액티비티

    private lateinit var viewModel: VisitPlaceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //방문지 수정 여부 체크
        val editPlace = intent.getSerializableExtra("editPlace") as VisitPlace?
        val isEdit = editPlace != null
        binding.isEdit = isEdit
        if (!isEdit) binding.tbVisitPlace.menu.clear() //방문지 수정이 아닐 경우 삭제 버튼 없애기

        viewModel =
            ViewModelProvider(
                this, VisitPlaceViewModel.VisitPlaceViewModelFactory(editPlace)
            )[VisitPlaceViewModel::class.java]
        binding.viewModel = viewModel

        //결과값 반환
        binding.btVisitPlaceConfirm.setOnClickListener {
            val intent = Intent(this, NewParkingLotActivity::class.java).apply {
                putExtra("visitPlace", viewModel.getVisitPlace())
                putExtra("isEdit", isEdit)
            }
            setResult(RESULT_OK, intent)
            finish()
        }

        binding.tbVisitPlace.setNavigationOnClickListener { finish() }

        //방문지 삭제 다이얼로그
        binding.tbVisitPlace.setOnMenuItemClickListener {
            if (it.itemId == R.id.menu_visitplace_delete) dialogDeleteVisitPlace()
            false
        }
    }

    private fun dialogDeleteVisitPlace() = CommonDialog(this,
        title = "방문지 삭제",
        message = "이 방문지를 삭제할까요?",
        cancelable = true,
        onPositive = {
            val intent = Intent(this, NewParkingLotActivity::class.java).apply {
                putExtra("deletePlace", viewModel.name.value!!)
            }
            setResult(RESULT_OK, intent)
            finish()
        }).show()
}