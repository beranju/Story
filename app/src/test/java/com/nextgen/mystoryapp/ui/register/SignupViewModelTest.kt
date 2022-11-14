package com.nextgen.mystoryapp.ui.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.nextgen.mystoryapp.data.common.util.ResponseWrapper
import com.nextgen.mystoryapp.data.signup.remote.dto.SignupRequest
import com.nextgen.mystoryapp.domain.common.base.SimpleResult
import com.nextgen.mystoryapp.domain.signup.SignupRepository
import com.nextgen.mystoryapp.domain.signup.usecase.SignupUseCase
import com.nextgen.mystoryapp.domain.story.usecase.StoryUseCase
import com.nextgen.mystoryapp.infra.utils.DataDummy
import com.nextgen.mystoryapp.infra.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import kotlin.math.sign

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class SignupViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()


    private val dummySignupResponse = DataDummy.generateSignupResponse()

    @Mock
    private lateinit var signupRepository: SignupRepository
    private lateinit var signupViewModel: SignupViewModel
    private lateinit var signupRequest: SignupRequest
    private lateinit var signupUseCase: SignupUseCase

    @Before
    fun setUp(){
        signupUseCase = SignupUseCase(signupRepository)
        signupViewModel = SignupViewModel(signupUseCase)
        signupRequest = SignupRequest("beran", "beran@gmail.com", "123456")
    }

    @Test
    fun `when signup success should not null and succes`() = runTest{
        val expected: Flow<SimpleResult<ResponseWrapper>> = flow {
            emit(SimpleResult.Success(dummySignupResponse.message))
        }
        `when`(signupUseCase.invoke(signupRequest)).thenReturn(expected)

        val actual = signupUseCase.invoke(signupRequest)
        Assert.assertEquals(expected, actual)
        assertNotNull(actual)
    }

    @Test
    fun `when signUp Error should Return Error`() = runTest {
        val expected: Flow<SimpleResult<ResponseWrapper>> = flow {
            signupRepository.signup(signupRequest).collect{error->
                if (error is SimpleResult.Error){
                    emit(SimpleResult.Error(error.rawResponse))
                }
            }
        }
        `when`(signupUseCase.invoke(signupRequest)).thenReturn(expected)
        val actualFlow = signupUseCase.invoke(signupRequest)
        assertEquals(expected, actualFlow)

    }

}