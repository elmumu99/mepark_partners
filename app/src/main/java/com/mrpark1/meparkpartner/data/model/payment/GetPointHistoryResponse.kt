package com.mrpark1.meparkpartner.data.model.payment

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetPointHistoryResponse(
    val TotalCount: Int,
    val ACount: Int,
    val BCount: Int,
    val NCount: Int,
    val Result: List<PointReceipt>
)