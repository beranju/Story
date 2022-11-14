package com.nextgen.mystoryapp.ui.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextgen.mystoryapp.data.common.util.ResponseWrapper
import com.nextgen.mystoryapp.data.signup.remote.dto.SignupRequest
import com.nextgen.mystoryapp.domain.common.base.SimpleResult
import com.nextgen.mystoryapp.domain.signup.usecase.SignupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(private val signupUseCase: SignupUseCase) : ViewModel() {
    private val state = MutableLiveData<RegisterState>(RegisterState.Init)
    val mState: LiveData<RegisterState> get() = state

    private fun setLoading() {
        state.value = RegisterState.IsLoading(true)
    }

    private fun hideLoading() {
        state.value = RegisterState.IsLoading(false)
    }

    private fun successRegister(message: String) {
        state.value = RegisterState.SuccessRegister(message)
    }

    private fun errorRegister(rawResponse: ResponseWrapper) {
        state.value = RegisterState.ErrorRegister(rawResponse)
    }

    fun signup(signupRequest: SignupRequest) {
        viewModelScope.launch {
            signupUseCase.invoke(signupRequest)
                .onStart {
                    setLoading()
                }
                .catch { e ->
                    hideLoading()
                    Log.e("SignUpViewModel", e.stackTraceToString())
                }
                .collect { result ->
                    hideLoading()
                    when (result) {
                        is SimpleResult.Success -> successRegister(result.message)
                        is SimpleResult.Error -> errorRegister(result.rawResponse)
                    }
                }
        }
    }
}

sealed class RegisterState {
    object Init : RegisterState()
    data class IsLoading(val isLoading: Boolean) : RegisterState()
    data class SuccessRegister(val message: String) : RegisterState()
    data class ErrorRegister(val rawResponse: ResponseWrapper) : RegisterState()

}