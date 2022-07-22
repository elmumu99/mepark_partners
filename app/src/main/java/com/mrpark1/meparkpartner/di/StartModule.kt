package com.mrpark1.meparkpartner.di

import android.content.Context
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.mrpark1.meparkpartner.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StartModule {
    //스플래시 화면 구현 시 DI를 위한 로직입니다.
    //Google One Tap 로그인시 기존 가입된 구글 계정만 보여줄지, 기기의 모든 구글 계정을 보여줄지(최초 사용)
    //선택할 수 있도록 GoogleIdTokenRequestOptions를 두 가지 옵션으로 주입할 수 있게 했습니다.

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class FilterByAuthorizedTrue

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class FilterByAuthorizedFalse

    @Singleton
    @FilterByAuthorizedTrue
    @Provides
    fun provideIdTokenRequestOptionsFilterTrue(): GoogleIdTokenRequestOptions =
        GoogleIdTokenRequestOptions.builder()
            .setSupported(true)
            .setServerClientId(Constants.SERVER_CLIENT_ID)
            .setFilterByAuthorizedAccounts(true)
            .build()

    @Singleton
    @FilterByAuthorizedFalse
    @Provides
    fun provideIdTokenRequestOptionsFilterFalse(): GoogleIdTokenRequestOptions =
        GoogleIdTokenRequestOptions.builder()
            .setSupported(true)
            .setServerClientId(Constants.SERVER_CLIENT_ID)
            .setFilterByAuthorizedAccounts(false)
            .build()

    @Singleton
    @FilterByAuthorizedTrue
    @Provides
    fun provideBeginSignInRequestFilterTrue(
        @FilterByAuthorizedTrue
        requestOptions: GoogleIdTokenRequestOptions
    ): BeginSignInRequest =
        BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(requestOptions)
            .setAutoSelectEnabled(true)
            .build()

    @Singleton
    @FilterByAuthorizedFalse
    @Provides
    fun provideBeginSignInRequestFilterFalse(
        @FilterByAuthorizedFalse
        requestOptions: GoogleIdTokenRequestOptions
    ): BeginSignInRequest =
        BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(requestOptions)
            .setAutoSelectEnabled(true)
            .build()

    @Singleton
    @Provides
    fun provideSignInClient(@ApplicationContext context: Context): SignInClient =
        Identity.getSignInClient(context)
}