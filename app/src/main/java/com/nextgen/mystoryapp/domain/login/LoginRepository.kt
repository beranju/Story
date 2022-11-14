package com.nextgen.mystoryapp.domain.login

import com.nextgen.mystoryapp.data.login.remote.dto.LoginRequest
import com.nextgen.mystoryapp.data.login.remote.dto.LoginResponse
import com.nextgen.mystoryapp.domain.common.base.BaseResult
import com.nextgen.mystoryapp.domain.login.entity.LoginEntity
import kotlinx.coroutines.flow.Flow

interface LoginRepository {
    suspend fun login(loginRequest: LoginRequest): Flow<BaseResult<LoginEntity, LoginResponse>>
}