package com.nextgen.mystoryapp.data.signup.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nextgen.mystoryapp.data.common.util.ResponseWrapper
import com.nextgen.mystoryapp.data.signup.remote.api.SignupApi
import com.nextgen.mystoryapp.data.signup.remote.dto.SignupRequest
import com.nextgen.mystoryapp.domain.common.base.SimpleResult
import com.nextgen.mystoryapp.domain.signup.SignupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SignupRepositoryImpl @Inject constructor(private val signupApi: SignupApi) :
    SignupRepository {
    override suspend fun signup(signupRequest: SignupRequest): Flow<SimpleResult<ResponseWrapper>> {
        return flow {
            val response = signupApi.register(signupRequest)
            if (response.isSuccessful) {
                val body = response.body()!!
                emit(SimpleResult.Success(body.message))
            } else {
                val type = object : TypeToken<ResponseWrapper>() {}.type
                val error: ResponseWrapper =
                    Gson().fromJson(response.errorBody()!!.charStream(), type)
                error.code = response.code()
                emit(SimpleResult.Error(error))
            }
        }
    }
}