package com.mrpark1.meparkpartner.util

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.github.dhaval2404.imagepicker.ImagePicker

object ImagePickerUtil {
    //이미지 피커 유틸
    fun pickImage(activity: Activity, result: ActivityResultLauncher<Intent>) {
        ImagePicker.with(activity)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .galleryMimeTypes(
                mimeTypes = arrayOf(
                    "image/png",
                    "image/jpg",
                    "image/jpeg"
                )
            )
            .galleryOnly()
            .createIntent { result.launch(it) }
    }
}