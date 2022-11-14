package com.nextgen.mystoryapp.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextgen.mystoryapp.data.login.remote.dto.LoginRequest
import com.nextgen.mystoryapp.data.login.remote.dto.LoginResponse
import com.nextgen.mystoryapp.domain.common.base.BaseResult
import com.nextgen.mystoryapp.domain.login.entity.LoginEntity
import com.nextgen.mystoryapp.domain.login.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginUseCase: LoginUseCase) : ViewModel() {
    private val state = MutableLiveData<LoginState>(LoginState.Init)
    val mState: LiveData<LoginState> get() = state

    private fun setLoading() {
        state.value = LoginState.IsLoading(true)
    }

    private fun hideLoading() {
        state.value = LoginState.IsLoading(false)
    }

    private fun successLogin(loginEntity: LoginEntity) {
        state.value = LoginState.SuccessLogin(loginEntity)
    }

    private fun errorLogin(rawResponse: LoginResponse) {
        state.value = LoginState.ErrorLogin(rawResponse)
    }

    fun login(loginRequest: LoginRequest) {
        viewModelScope.launch {
            loginUseCase.invoke(loginRequest)
                .onStart {
                    setLoading()
                }
                .catch { e ->
                    hideLoading()
                    Log.e("LoginViewModelTest", e.stackTraceToString())
                }
                .collect { result ->
                    hideLoading()
                    when (result) {
                        is BaseResult.Success -> successLogin(result.data)
                        is BaseResult.Error -> errorLogin(result.rawResponse)
                    }
                }
        }
    }
}

sealed class LoginState {
    object Init : LoginState()
    data class IsLoading(val isLoading: Boolean) : LoginState()
    data class SuccessLogin(val loginEntity: LoginEntity) : LoginState()
    data class ErrorLogin(val rawResponse: LoginResponse) : LoginState()

}