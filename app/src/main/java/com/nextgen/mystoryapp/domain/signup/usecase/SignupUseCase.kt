package com.nextgen.mystoryapp.domain.signup.usecase

import com.nextgen.mystoryapp.data.common.util.ResponseWrapper
import com.nextgen.mystoryapp.data.signup.remote.dto.SignupRequest
import com.nextgen.mystoryapp.domain.common.base.SimpleResult
import com.nextgen.mystoryapp.domain.signup.SignupRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignupUseCase @Inject constructor(private val signupRepository: SignupRepository) {
    suspend fun invoke(signupRequest: SignupRequest): Flow<SimpleResult<ResponseWrapper>> {
        return signupRepository.signup(signupRequest)
    }
}