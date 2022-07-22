package com.mrpark1.meparkpartner.ui.insurance

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.mrpark1.meparkpartner.R

//선택 화면 구현을 위한 BindingAdapter (현재 NewParkingLotActivity에서 사용 중일 것임)
@BindingAdapter("bindTextColorSelected")
fun bindTextColorSelected(textView: TextView, selected: Boolean) {
    textView.setTextColor(
        ContextCompat.getColor(
            textView.context,
            if (selected) R.color.white else R.color.materialGray8
        )
    )
}

@BindingAdapter("bindBackgroundColorSelected")
fun bindBackgroundColorSelected(view: View, selected: Boolean) {
    view.backgroundTintList = ContextCompat.getColorStateList(
        view.context,
        if (selected) R.color.brandBlue else R.color.materialGray3
    )
}

@BindingAdapter("bindBackgroundColorSelectedRed")
fun bindBackgroundColorSelectedRed(view: View, selected: Boolean) {
    if (!selected) view.backgroundTintList = null
    else view.backgroundTintList = ContextCompat.getColorStateList(
        view.context,
        R.color.red
    )
}