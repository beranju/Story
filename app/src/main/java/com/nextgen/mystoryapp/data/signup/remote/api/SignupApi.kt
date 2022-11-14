package com.nextgen.mystoryapp.data.signup.remote.api

import com.nextgen.mystoryapp.data.common.util.ResponseWrapper
import com.nextgen.mystoryapp.data.signup.remote.dto.SignupRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SignupApi {
    @POST("register")
    suspend fun register(@Body signupRequest: SignupRequest): Response<ResponseWrapper>
}