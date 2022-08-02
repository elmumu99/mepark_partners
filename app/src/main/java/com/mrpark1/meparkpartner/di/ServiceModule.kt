package com.mrpark1.meparkpartner.di

import com.mrpark1.meparkpartner.data.service.ManageSaleService
import com.mrpark1.meparkpartner.data.service.ParkingLotService
import com.mrpark1.meparkpartner.data.service.PartnerService
import com.mrpark1.meparkpartner.data.service.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    //작성한 Service들을 이곳에서 구현하여 Repository 등에 주입합니다.

    @Singleton
    @Provides
    fun provideUserService(retrofit: Retrofit): UserService =
        retrofit.create(UserService::class.java)

    @Singleton
    @Provides
    fun providePartnerService(retrofit: Retrofit): PartnerService =
        retrofit.create(PartnerService::class.java)

    @Singleton
    @Provides
    fun provideParkingLotService(retrofit: Retrofit): ParkingLotService =
        retrofit.create(ParkingLotService::class.java)

    @Singleton
    @Provides
    fun provideManageSaleService(retrofit: Retrofit): ManageSaleService =
        retrofit.create(ManageSaleService::class.java)
}


