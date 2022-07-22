package com.mrpark1.meparkpartner.util

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class TokenInterceptor @Inject constructor(private val sharedPrefUtil: SharedPrefUtil) :
    Interceptor {
    //Retrofit 요청 시 헤더에 세션 토큰 첨부

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sharedPrefUtil.getString(SharedPrefUtil.KEY_ACCESS_TOKEN, null)
        val request = chain.request()
            .newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        Log.d("TEST@","token :: $token")
        return chain.proceed(request)
    }
}