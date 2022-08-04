package com.mrpark1.meparkpartner.data.model.common

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
open class BaseResponse {
    var message: String = ""
}