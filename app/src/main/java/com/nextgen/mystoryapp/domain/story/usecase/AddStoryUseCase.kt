package com.nextgen.mystoryapp.domain.story.usecase

import com.nextgen.mystoryapp.data.common.util.ResponseWrapper
import com.nextgen.mystoryapp.domain.common.base.SimpleResult
import com.nextgen.mystoryapp.domain.story.StoryRepository
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class AddStoryUseCase @Inject constructor(private val storyRepository: StoryRepository) {
    suspend fun invoke(
        photo: MultipartBody.Part,
        desc: RequestBody,
        lat: Float,
        lon: Float,
    ): Flow<SimpleResult<ResponseWrapper>> {
        return storyRepository.postStory(photo, desc, lat, lon)
    }
}