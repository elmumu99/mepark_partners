package com.mrpark1.meparkpartner.ui.common

import android.net.Uri
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.mrpark1.meparkpartner.R

//자주 쓰이는 BindingAdapter 모음
@BindingAdapter("bindVisibility")
fun bindVisibility(v: View, b: Boolean) {
    v.visibility = if (b) View.VISIBLE else View.GONE
}

@BindingAdapter("bindInvisibility")
fun bindInvisibility(v: View, b: Boolean) {
    v.visibility = if (b) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("bindImageUrl")
fun bindImageUrl(imageView: ImageView, url: String?) {
    if (url.isNullOrEmpty()) return
    Glide.with(imageView).load(url).into(imageView)
}

@BindingAdapter("bindImageByte")
fun bindImageByte(imageView: ImageView, byte: String?) {
    //TODO: change thread
    if (byte.isNullOrEmpty()) return
    val imageByteArray: ByteArray = Base64.decode(byte, Base64.DEFAULT)
    Glide.with(imageView).asBitmap().load(imageByteArray).into(imageView)
}

@BindingAdapter("bindImageUri")
fun bindImageUri(imageView: ImageView, uri: Uri?) {
    if (uri != null) imageView.setImageURI(uri)
}

@BindingAdapter("bindButtonEnabled")
fun bindButtonEnabled(button: Button, enabled: Boolean) {
    button.run {
        backgroundTintList = ContextCompat.getColorStateList(
            context,
            if (enabled) R.color.brandBlue else R.color.materialGray4
        )
        isEnabled = enabled
    }
}

@BindingAdapter("bindDoOnTextChanged")
fun bindDoOnTextChanged(editText: EditText, job: () -> Unit) {
    editText.doOnTextChanged { _, _, _, _ -> job() }
}
