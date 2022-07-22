package com.mrpark1.meparkpartner.ui.dialogs

import androidx.lifecycle.ViewModel

class CommonDialogViewModel : ViewModel() {
    var title: String? = null
    var message: String = ""
    var positiveText: String = ""
    var negativeText: String? = null
    var cancelable: Boolean = true
}