package com.nextgen.mystoryapp.domain.signup

import com.nextgen.mystoryapp.data.common.util.ResponseWrapper
import com.nextgen.mystoryapp.data.signup.remote.dto.SignupRequest
import com.nextgen.mystoryapp.domain.common.base.SimpleResult
import kotlinx.coroutines.flow.Flow

interface SignupRepository {
    suspend fun signup(signupRequest: SignupRequest): Flow<SimpleResult<ResponseWrapper>>
}