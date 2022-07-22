package com.mrpark1.meparkpartner.data.repository

import com.mrpark1.meparkpartner.data.model.parkinglot.*
import com.mrpark1.meparkpartner.data.model.parkinglot.visitplace.AddMyVisitPlacesRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.visitplace.AddMyVisitPlacesResponse
import com.mrpark1.meparkpartner.data.model.parkinglot.visitplace.RemoveMyVisitPlaceRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.visitplace.RemoveMyVisitPlaceResponse
import retrofit2.Response

interface NewParkingLotRepository {
    suspend fun addMyParkingLot(addMyParkingLotRequest: AddMyParkingLotRequest): Response<AddMyParkingLotResponse>
    suspend fun addMyVisitPlace(addMyVisitPlacesRequest: AddMyVisitPlacesRequest): Response<AddMyVisitPlacesResponse>
    suspend fun addParkingLotQrcode(addParkingLotQrcodeRequest: AddParkingLotQrcodeRequest): Response<AddParkingLotQrcodeResponse>
    suspend fun updateMyParkingLot(updateMyParkingLotRequest: UpdateMyParkingLotRequest): Response<UpdateMyParkingLotResponse>
    suspend fun removeMyVisitPlace(removeMyVisitPlaceRequest: RemoveMyVisitPlaceRequest): Response<RemoveMyVisitPlaceResponse>
}