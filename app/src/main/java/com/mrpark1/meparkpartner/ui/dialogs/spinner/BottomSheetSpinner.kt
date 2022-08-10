package com.mrpark1.meparkpartner.ui.dialogs.spinner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.databinding.DialogBottomSheetSpinnerBinding

class BottomSheetSpinner : BottomSheetDialogFragment() {
    //화면 아래에서 나오는 선택지 다이얼로그

    private lateinit var binding: DialogBottomSheetSpinnerBinding
    private val viewModel: BottomSheetSpinnerViewModel by activityViewModels()

    private var title: String? = null
    private lateinit var desc: String
    private var items: ArrayList<String> = arrayListOf()
    private var counts: ArrayList<Int> = arrayListOf()
    private lateinit var onItemClick: (Int) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.dialog_bottom_sheet_spinner,
            container,
            false
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        title?.let {
            viewModel.title = it
            viewModel.desc = desc
            viewModel.items.clear()
            viewModel.items.addAll(items)
            viewModel.counts.clear()
            viewModel.counts.addAll(counts)
            viewModel.onItemClick = onItemClick
        }

        binding.rvBottomsheetSpinner.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBottomsheetSpinner.adapter = BottomSheetSpinnerAdapter {
            viewModel.onItemClick(it)
            dismiss()
        }

        binding.btBottomsheetSpinnerClose.setOnClickListener { dismiss() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (dialog as? BottomSheetDialog)?.behavior?.state = STATE_EXPANDED
    }

    fun setInfo(
        title: String,
        items: List<String>,
        onItemClick: (Int) -> Unit,
        desc: String = "",
    ): BottomSheetSpinner {
        this.title = title
        this.desc = desc
        this.items.clear()
        this.items.addAll(items)
        this.onItemClick = onItemClick
        return this
    }

    fun setCountInfo(
        title: String,
        items: List<String>,
        onItemClick: (Int) -> Unit,
        desc: String = "",
        counts: List<Int>,
    ): BottomSheetSpinner {
        this.title = title
        this.desc = desc
        this.items.clear()
        this.items.addAll(items)
        this.counts.clear()
        this.counts.addAll(counts)
        this.onItemClick = onItemClick
        return this
    }
}