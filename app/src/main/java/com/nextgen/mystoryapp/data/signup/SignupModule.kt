package com.nextgen.mystoryapp.data.signup

import com.nextgen.mystoryapp.data.common.module.NetworkModule
import com.nextgen.mystoryapp.data.signup.remote.api.SignupApi
import com.nextgen.mystoryapp.data.signup.repository.SignupRepositoryImpl
import com.nextgen.mystoryapp.domain.signup.SignupRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
class SignupModule {

    @Singleton
    @Provides
    fun provideSignupApi(retrofit: Retrofit): SignupApi {
        return retrofit.create(SignupApi::class.java)
    }

    @Singleton
    @Provides
    fun provideSignupRepository(signupApi: SignupApi): SignupRepository {
        return SignupRepositoryImpl(signupApi)
    }
}