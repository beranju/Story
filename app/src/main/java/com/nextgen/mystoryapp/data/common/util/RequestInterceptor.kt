package com.nextgen.mystoryapp.data.common.util

import com.nextgen.mystoryapp.infra.utils.SharedPrefs
import okhttp3.Interceptor
import okhttp3.Response

class RequestInterceptor constructor(private val prefs: SharedPrefs) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = prefs.getToken()
        val newReq =
            chain.request().newBuilder().addHeader("Authorization", "Bearer $token").build()
        return chain.proceed(newReq)
    }
}