package com.nextgen.mystoryapp.ui.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextgen.mystoryapp.data.common.util.ResponseWrapper
import com.nextgen.mystoryapp.domain.common.base.SimpleResult
import com.nextgen.mystoryapp.domain.story.usecase.AddStoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject


@HiltViewModel
class AddStoryViewModel @Inject constructor(private val addStoryUseCase: AddStoryUseCase) :
    ViewModel() {
    private var state = MutableLiveData<AddStoryState>(AddStoryState.Init)
    val mState: LiveData<AddStoryState> get() = state

    private fun setLoading() {
        state.value = AddStoryState.IsLoading(true)
    }

    private fun hideLoading() {
        state.value = AddStoryState.IsLoading(false)
    }

    private fun showToast(message: String) {
        state.value = AddStoryState.ShowToast(message)
    }

    private fun successAdd(message: String) {
        state.value = AddStoryState.SuccessAdd(message)
    }

    private fun erorrAdd(rawResponse: ResponseWrapper) {
        state.value = AddStoryState.ErrorAdd(rawResponse)
    }

    fun addStory(photo: MultipartBody.Part, desc: RequestBody, lat: Float, lon: Float) {
        viewModelScope.launch {
            addStoryUseCase.invoke(photo, desc, lat, lon)
                .onStart {
                    setLoading()
                }
                .catch { e ->
                    hideLoading()
                    showToast(e.stackTraceToString())
                }
                .collect { result ->
                    hideLoading()
                    when (result) {
                        is SimpleResult.Success -> successAdd(result.message)
                        is SimpleResult.Error -> erorrAdd(result.rawResponse)
                    }
                }
        }
    }


}

sealed class AddStoryState {
    object Init : AddStoryState()
    data class IsLoading(val isLoading: Boolean) : AddStoryState()
    data class ShowToast(val message: String) : AddStoryState()
    data class SuccessAdd(val message: String) : AddStoryState()
    data class ErrorAdd(val rawResponse: ResponseWrapper) : AddStoryState()

}