package com.mrpark1.meparkpartner.data.service

import com.mrpark1.meparkpartner.data.model.common.ParkingLot
import com.mrpark1.meparkpartner.data.model.parkinglot.*
import com.mrpark1.meparkpartner.data.model.parkinglot.car.*
import com.mrpark1.meparkpartner.data.model.parkinglot.commute.UpdateCommutingRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.commute.UpdateCommutingResponse
import com.mrpark1.meparkpartner.data.model.parkinglot.visitplace.AddMyVisitPlacesRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.visitplace.AddMyVisitPlacesResponse
import com.mrpark1.meparkpartner.data.model.parkinglot.visitplace.RemoveMyVisitPlaceRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.visitplace.RemoveMyVisitPlaceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ParkingLotService {
    //주차장과 관련된 API interface를 모아둡니다.

    @POST("/AddMyParkingLot")
    suspend fun addMyParkingLot(@Body request: AddMyParkingLotRequest): Response<AddMyParkingLotResponse>

    @POST("/GetMyParkingLots")
    suspend fun getMyParkingLots(@Body request: GetMyParkingLotsRequest): Response<List<ParkingLot>>

    @POST("/UpdateMyParkingLot")
    suspend fun updateMyParkingLot(@Body request: UpdateMyParkingLotRequest): Response<UpdateMyParkingLotResponse>

    @POST("/RemoveMyParkingLot")
    suspend fun removeMyParkingLot(@Body request: RemoveMyParkingLotRequest): Response<RemoveMyParkingLotResponse>

    @POST("/AddParkingLotQrcode")
    suspend fun addParkingLotQrcode(@Body request: AddParkingLotQrcodeRequest): Response<AddParkingLotQrcodeResponse>

    //VisitPlace

    @POST("/AddMyVisitPlaces")
    suspend fun addMyVisitPlaces(@Body request: AddMyVisitPlacesRequest): Response<AddMyVisitPlacesResponse>

    @POST("/RemoveMyVisitPlace")
    suspend fun removeMyVisitPlace(@Body request: RemoveMyVisitPlaceRequest): Response<RemoveMyVisitPlaceResponse>

    //Car

    @POST("/AddEntranceAndExitCar")
    suspend fun addEntranceAndExitCar(@Body request: AddEntranceAndExitCarRequest): Response<AddEntranceAndExitCarResponse>

    @POST("/UpdateEntranceAndExitCar")
    suspend fun updateEntranceAndExitCar(@Body request: UpdateEntranceAndExitCarRequest): Response<UpdateEntranceAndExitCarResponse>

    @POST("/GetParkedCars")
    suspend fun getParkedCars(@Body request: GetParkedCarsRequest): Response<GetParkedCarsResponse>

    @POST("/GetParkHistory")
    suspend fun getParkHistory(@Body request: GetParkHistoryRequest): Response<GetParkHistoryResponse>

    //Commute

    @POST("/UpdateCommuting")
    suspend fun updateCommuting(@Body request: UpdateCommutingRequest): Response<UpdateCommutingResponse>


    //월주차
    @POST("/AddMonthParkedCar")
    suspend fun addMonthParkedCar(@Body request: AddMonthParkedCarRequest): Response<AddMonthParkedCarResponse>

    @POST("/GetMonthParkedCars")
    suspend fun getMonthParkedCars(@Body request: GetMonthParkedCarsRequest): Response<GetMonthParkedCarsResponse>

    @POST("/UpdateMonthParkedCar")
    suspend fun updateMonthParkedCar(@Body request: UpdateMonthParkedCarRequest): Response<UpdateMonthParkedCarResponse>

    @POST("/RemoveMonthParkedCar")
    suspend fun removeMonthParkedCar(@Body request: RemoveMonthParkedCarRequest): Response<RemoveMonthParkedCarResponse>
}