package com.mrpark1.meparkpartner.data.model.partner

import com.mrpark1.meparkpartner.data.model.common.ParkingLot
import com.mrpark1.meparkpartner.data.model.common.ParkingLot2
import com.mrpark1.meparkpartner.data.model.common.Partner
import com.mrpark1.meparkpartner.data.model.common.PartnerUser
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetMyPartnerInfoResponse (
    val Partner: Partner,
    val ParkingLots: List<ParkingLot2>
)