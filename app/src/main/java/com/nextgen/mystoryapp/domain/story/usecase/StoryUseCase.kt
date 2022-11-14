package com.nextgen.mystoryapp.domain.story.usecase

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.nextgen.mystoryapp.data.story.remote.dto.StoryItem
import com.nextgen.mystoryapp.data.story.remote.dto.StoryResponse
import com.nextgen.mystoryapp.domain.common.base.BaseResult
import com.nextgen.mystoryapp.domain.story.StoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StoryUseCase @Inject constructor(private val storyRepository: StoryRepository) {
    suspend fun getStory(): LiveData<PagingData<StoryItem>> {
        return storyRepository.getStories()
    }

    suspend fun invoke(loc: Int): Flow<BaseResult<StoryResponse, StoryResponse>> {
        return storyRepository.getStoriesLocation(loc)
    }
}