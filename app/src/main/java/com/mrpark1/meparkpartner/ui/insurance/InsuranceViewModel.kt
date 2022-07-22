package com.mrpark1.meparkpartner.ui.insurance

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class InsuranceViewModel : ViewModel() {

    val selectedPlan = MutableLiveData("N")

    fun setSelectedPlan(selected: String) {
        selectedPlan.value = selected
    }

    private var initialized = false

    fun initialize(currentPlan: String?) {
        if (initialized) return
        selectedPlan.value = currentPlan
        initialized = true
    }

}