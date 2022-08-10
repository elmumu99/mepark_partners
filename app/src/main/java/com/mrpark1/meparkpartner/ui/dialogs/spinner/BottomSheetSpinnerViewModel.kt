package com.mrpark1.meparkpartner.ui.dialogs.spinner

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.ViewModel

class BottomSheetSpinnerViewModel : ViewModel() {
    val items = ObservableArrayList<String>()
    val counts = ObservableArrayList<Int>()
    var title = ""
    var desc = ""
    var onItemClick: (Int) -> Unit = {}
}