package com.nextgen.mystoryapp.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextgen.mystoryapp.data.story.remote.dto.StoryResponse
import com.nextgen.mystoryapp.domain.common.base.BaseResult
import com.nextgen.mystoryapp.domain.story.usecase.StoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(private val storyUseCase: StoryUseCase) : ViewModel() {
    private val state = MutableLiveData<MapState>(MapState.Init)
    val mState: LiveData<MapState> get() = state

    private fun setLoading() {
        state.value = MapState.IsLoading(true)
    }

    private fun hideLoading() {
        state.value = MapState.IsLoading(false)
    }

    private fun showToast(message: String) {
        state.value = MapState.ShowToast(message)
    }

    private fun successGetStory(storyEntity: StoryResponse) {
        state.value = MapState.Success(storyEntity)
    }

    private fun errorGetStory(rawResponse: StoryResponse) {
        state.value = MapState.Error(rawResponse)
    }

    fun getStoriesLocation(loc: Int) {
        viewModelScope.launch {
            storyUseCase.invoke(loc)
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
                        is BaseResult.Success -> successGetStory(result.data)
                        is BaseResult.Error -> errorGetStory(result.rawResponse)
                    }
                }
        }

    }

}

sealed class MapState {
    object Init : MapState()
    data class IsLoading(val isLoading: Boolean) : MapState()
    data class ShowToast(val message: String) : MapState()
    data class Success(val storyEntity: StoryResponse) : MapState()
    data class Error(val rawResponse: StoryResponse) : MapState()
}