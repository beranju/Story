package com.nextgen.mystoryapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.nextgen.mystoryapp.data.story.remote.dto.StoryItem
import com.nextgen.mystoryapp.data.story.remote.dto.StoryResponse
import com.nextgen.mystoryapp.domain.common.base.BaseResult
import com.nextgen.mystoryapp.domain.story.usecase.StoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val storyUseCase: StoryUseCase) : ViewModel() {
    private val state = MutableLiveData<StoryState>(StoryState.Init)
    val mState: LiveData<StoryState> get() = state

    private var mStory = MutableLiveData<PagingData<StoryItem>>()
    val story: LiveData<PagingData<StoryItem>> get() = mStory

    init {
        getStories()
    }

    fun getStories() {
        viewModelScope.launch {
            mStory = storyUseCase.getStory() as MutableLiveData<PagingData<StoryItem>>
        }
    }

    private fun showToast(message: String) {
        state.value = StoryState.ShowToast(message)
    }

    private fun successGetStory(storyEntity: StoryResponse) {
        state.value = StoryState.Success(storyEntity)
    }

    private fun errorGetStory(rawResponse: StoryResponse) {
        state.value = StoryState.Error(rawResponse)
    }

    fun getStoriesLocation(loc: Int) {
        viewModelScope.launch {
            storyUseCase.invoke(loc)
                .catch { e ->
                    showToast(e.stackTraceToString())
                }
                .collect { result ->
                    when (result) {
                        is BaseResult.Success -> successGetStory(result.data)
                        is BaseResult.Error -> errorGetStory(result.rawResponse)
                    }
                }
        }
    }
}

sealed class StoryState {
    object Init : StoryState()
    data class ShowToast(val message: String) : StoryState()
    data class Success(val storyEntity: StoryResponse) : StoryState()
    data class Error(val rawResponse: StoryResponse) : StoryState()
}
