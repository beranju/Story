package com.nextgen.mystoryapp.domain.story.usecase

import com.nextgen.mystoryapp.data.story.remote.dto.DetailResponse
import com.nextgen.mystoryapp.domain.common.base.BaseResult
import com.nextgen.mystoryapp.domain.story.StoryRepository
import com.nextgen.mystoryapp.domain.story.entity.StoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DetailStoryUseCase @Inject constructor(private val storyRepository: StoryRepository) {
    suspend fun invoke(id: String): Flow<BaseResult<StoryEntity, DetailResponse>> {
        return storyRepository.getStoryById(id)
    }

}