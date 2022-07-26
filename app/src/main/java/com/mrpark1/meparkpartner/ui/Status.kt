package com.mrpark1.meparkpartner.ui

enum class Status {
    //각 액티비티에서 사용하는 상태 정보를 담은 enum

    //Common
    INIT,
    LOADING,
    SUCCESS,
    ERROR,
    ERROR_INTERNET,
    ERROR_EXPIRED,

    //Start
    START_LOGIC_STARTED,
    START_FIRST_LAUNCH,
    START_NEW_USER,
    START_NO_PARTNER,
    START_ERROR_ONE_TAP_CANCELED,
    START_ERROR_NO_GOOGLE,

    //NoPartner
    NOPART_NO_PARTNER,
    NOPART_INVITED,
    NOPART_PENDING,

    //NewPartner
    NEWPART_ERROR_PHOTO,
    NEWPART_PHOTO_PICK,
    NEWPART_ACCOUNT_CHECK,

    //NewParkingLot
    NEWPARK_PLACEHOLDER,
    NEWPARK_ERROR_PROFILE,
    NEWPARK_ERROR_VISITPLACE,
    NEWPARK_ERROR_QR,
    NEWPARK_NEED_VISITPLACE,

    //MigrateUser
    MIGRATE_AUTH_CODE_SENT,
    MIGRATE_ERROR_INVALID_EMAIL,
    MIGRATE_ERROR_WRONG_AUTH_CODE,

    //Main
    MAIN_NO_PARTNER,
    MAIN_NO_PARKING_LOTS,

    //Park
    PARK_EXIT_SUCCESS,

    //ParkHistory
    PARKHIS_NO_CARS,

    //AddPartnerUser
    ADD_PARTNER_USER,
    UPDATE_PARTNER_USER,

    //ManageSale
    MANAGE_SALE_PARTNER,
    MANAGE_SALE_PARKING_LOT,


    //MonthPark
    MONTH_PARK_UPDATE_SUCCESS,
    MONTH_PARK_DELETE_SUCCESS
}