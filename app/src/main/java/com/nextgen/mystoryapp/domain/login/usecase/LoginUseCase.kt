package com.nextgen.mystoryapp.domain.login.usecase

import com.nextgen.mystoryapp.data.login.remote.dto.LoginRequest
import com.nextgen.mystoryapp.data.login.remote.dto.LoginResponse
import com.nextgen.mystoryapp.domain.common.base.BaseResult
import com.nextgen.mystoryapp.domain.login.LoginRepository
import com.nextgen.mystoryapp.domain.login.entity.LoginEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val loginRepository: LoginRepository) {
    suspend fun invoke(loginRequest: LoginRequest): Flow<BaseResult<LoginEntity, LoginResponse>> {
        return loginRepository.login(loginRequest)
    }
}