package com.nextgen.mystoryapp.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextgen.mystoryapp.data.story.remote.dto.DetailResponse
import com.nextgen.mystoryapp.domain.common.base.BaseResult
import com.nextgen.mystoryapp.domain.story.entity.StoryEntity
import com.nextgen.mystoryapp.domain.story.usecase.DetailStoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val detailStoryUseCase: DetailStoryUseCase) :
    ViewModel() {
    private var state = MutableLiveData<DetailState>(DetailState.Init)
    val mState: LiveData<DetailState> get() = state

    private fun setLoading() {
        state.value = DetailState.IsLoading(true)
    }

    private fun hideLoading() {
        state.value = DetailState.IsLoading(false)
    }

    private fun success(storyEntity: StoryEntity) {
        state.value = DetailState.Success(storyEntity)
    }

    private fun error(rawResponse: DetailResponse) {
        state.value = DetailState.Error(rawResponse)
    }

    fun getStoryById(id: String) {
        viewModelScope.launch {
            detailStoryUseCase.invoke(id)
                .onStart {
                    setLoading()
                }
                .catch { e ->
                    hideLoading()
                    error(e.stackTraceToString())
                }
                .collect { result ->
                    hideLoading()
                    when (result) {
                        is BaseResult.Success -> success(result.data)
                        is BaseResult.Error -> error(result.rawResponse)
                    }
                }
        }
    }
}

sealed class DetailState {
    object Init : DetailState()
    data class IsLoading(val isLoading: Boolean) : DetailState()
    data class Success(val storyEntity: StoryEntity) : DetailState()
    data class Error(val rawResponse: DetailResponse) : DetailState()
}