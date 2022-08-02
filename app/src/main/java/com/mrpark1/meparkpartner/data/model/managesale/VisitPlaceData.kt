package com.mrpark1.meparkpartner.data.model.managesale

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class VisitPlaceData(
    val VisitPlace: String,
    val VisitPlaceProfit: Int,
    val VisitPlaceTotalIN: Int,
    val VisitPlaceTotalReturn: Int
): Serializable
