package com.nextgen.mystoryapp.data.login.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nextgen.mystoryapp.data.login.remote.api.LoginApi
import com.nextgen.mystoryapp.data.login.remote.dto.LoginRequest
import com.nextgen.mystoryapp.data.login.remote.dto.LoginResponse
import com.nextgen.mystoryapp.domain.common.base.BaseResult
import com.nextgen.mystoryapp.domain.login.LoginRepository
import com.nextgen.mystoryapp.domain.login.entity.LoginEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(private val loginApi: LoginApi) : LoginRepository {
    override suspend fun login(loginRequest: LoginRequest): Flow<BaseResult<LoginEntity, LoginResponse>> {
        return flow {
            val response = loginApi.login(loginRequest)
            if (response.isSuccessful) {
                val body = response.body()!!
                val loginEntity = LoginEntity(body.loginResult?.userId!!,
                    body.loginResult?.name!!,
                    body.loginResult?.token!!)
                emit(BaseResult.Success(loginEntity))
            } else {
                val type = object : TypeToken<LoginResponse>() {}.type
                val error: LoginResponse =
                    Gson().fromJson(response.errorBody()!!.charStream(), type)
                error.code = response.code()
                emit(BaseResult.Error(error))
            }
        }
    }
}