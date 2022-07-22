package com.mrpark1.meparkpartner.data.repository.implementations

import com.mrpark1.meparkpartner.data.model.parkinglot.*
import com.mrpark1.meparkpartner.data.model.parkinglot.visitplace.AddMyVisitPlacesRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.visitplace.AddMyVisitPlacesResponse
import com.mrpark1.meparkpartner.data.model.parkinglot.visitplace.RemoveMyVisitPlaceRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.visitplace.RemoveMyVisitPlaceResponse
import com.mrpark1.meparkpartner.data.repository.NewParkingLotRepository
import com.mrpark1.meparkpartner.data.service.ParkingLotService
import retrofit2.Response
import javax.inject.Inject

class NewParkingLotRepositoryImpl @Inject constructor(
    private val parkingLotService: ParkingLotService
) : NewParkingLotRepository {

    override suspend fun addMyParkingLot(addMyParkingLotRequest: AddMyParkingLotRequest): Response<AddMyParkingLotResponse> =
        parkingLotService.addMyParkingLot(addMyParkingLotRequest)

    override suspend fun addMyVisitPlace(addMyVisitPlacesRequest: AddMyVisitPlacesRequest): Response<AddMyVisitPlacesResponse> =
        parkingLotService.addMyVisitPlaces(addMyVisitPlacesRequest)

    override suspend fun addParkingLotQrcode(addParkingLotQrcodeRequest: AddParkingLotQrcodeRequest): Response<AddParkingLotQrcodeResponse> =
        parkingLotService.addParkingLotQrcode(addParkingLotQrcodeRequest)

    override suspend fun updateMyParkingLot(updateMyParkingLotRequest: UpdateMyParkingLotRequest): Response<UpdateMyParkingLotResponse> =
        parkingLotService.updateMyParkingLot(updateMyParkingLotRequest)

    override suspend fun removeMyVisitPlace(removeMyVisitPlaceRequest: RemoveMyVisitPlaceRequest): Response<RemoveMyVisitPlaceResponse> =
        parkingLotService.removeMyVisitPlace(removeMyVisitPlaceRequest)

}