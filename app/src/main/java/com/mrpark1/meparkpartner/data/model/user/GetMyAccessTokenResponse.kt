package com.mrpark1.meparkpartner.data.model.user

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetMyAccessTokenResponse(
    val AccessToken: String
)