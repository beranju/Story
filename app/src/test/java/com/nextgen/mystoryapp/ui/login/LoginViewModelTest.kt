package com.nextgen.mystoryapp.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nextgen.mystoryapp.data.login.remote.dto.LoginRequest
import com.nextgen.mystoryapp.data.login.remote.dto.LoginResponse
import com.nextgen.mystoryapp.domain.common.base.BaseResult
import com.nextgen.mystoryapp.domain.common.base.SimpleResult
import com.nextgen.mystoryapp.domain.login.LoginRepository
import com.nextgen.mystoryapp.domain.login.entity.LoginEntity
import com.nextgen.mystoryapp.domain.login.usecase.LoginUseCase
import com.nextgen.mystoryapp.domain.signup.SignupRepository
import com.nextgen.mystoryapp.infra.utils.DataDummy
import com.nextgen.mystoryapp.infra.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import kotlin.math.log

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val dataLogin = DataDummy.generateLoginEntity()

    @Mock
    private lateinit var loginRepository: LoginRepository
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var loginUseCase: LoginUseCase
    private lateinit var loginRequest: LoginRequest



    @Before
    fun setUp() {
        loginUseCase = LoginUseCase(loginRepository)
        loginViewModel = LoginViewModel(loginUseCase)
        loginRequest = LoginRequest("beran@gmail.com", "123456")

    }

    @Test
    fun `when login success should not null and return BaseResult success`() = runTest{
        val expected: Flow<BaseResult<LoginEntity, LoginResponse>> = flow {
            emit(BaseResult.Success(dataLogin))
        }
        `when`(loginUseCase.invoke(loginRequest)).thenReturn(expected)
        val actual = loginUseCase.invoke(loginRequest)
        assertEquals(expected, actual)
        assertNotNull(actual)

    }

    @Test
    fun `when login error should return error`() = runTest{
        val expected: Flow<BaseResult<LoginEntity, LoginResponse>> = flow {
            loginRepository.login(loginRequest).collect{
                if (it is BaseResult.Error){
                    emit(BaseResult.Error(it.rawResponse))
                }
            }
        }
        `when`(loginUseCase.invoke(loginRequest)).thenReturn(expected)
        val actual = loginUseCase.invoke(loginRequest)
        assertEquals(expected,actual)
    }
}