package com.nextgen.mystoryapp.domain.story

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.nextgen.mystoryapp.data.common.util.ResponseWrapper
import com.nextgen.mystoryapp.data.story.remote.dto.DetailResponse
import com.nextgen.mystoryapp.data.story.remote.dto.StoryItem
import com.nextgen.mystoryapp.data.story.remote.dto.StoryResponse
import com.nextgen.mystoryapp.domain.common.base.BaseResult
import com.nextgen.mystoryapp.domain.common.base.SimpleResult
import com.nextgen.mystoryapp.domain.story.entity.StoryEntity
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface StoryRepository {
    suspend fun getStories(): LiveData<PagingData<StoryItem>>

    suspend fun getStoriesLocation(loc: Int): Flow<BaseResult<StoryResponse, StoryResponse>>

    suspend fun getStoryById(id: String): Flow<BaseResult<StoryEntity, DetailResponse>>

    suspend fun postStory(
        photo: MultipartBody.Part,
        desc: RequestBody,
        lat: Float,
        lon: Float,
    ): Flow<SimpleResult<ResponseWrapper>>
}