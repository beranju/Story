package com.nextgen.mystoryapp.data.story.repository

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nextgen.mystoryapp.data.common.util.ResponseWrapper
import com.nextgen.mystoryapp.data.story.local.database.StoryDatabase
import com.nextgen.mystoryapp.data.story.mediator.StoryRemoteMediator
import com.nextgen.mystoryapp.data.story.remote.api.StoryApi
import com.nextgen.mystoryapp.data.story.remote.dto.DetailResponse
import com.nextgen.mystoryapp.data.story.remote.dto.StoryItem
import com.nextgen.mystoryapp.data.story.remote.dto.StoryResponse
import com.nextgen.mystoryapp.domain.common.base.BaseResult
import com.nextgen.mystoryapp.domain.common.base.SimpleResult
import com.nextgen.mystoryapp.domain.story.StoryRepository
import com.nextgen.mystoryapp.domain.story.entity.StoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class StoryRepositoryImpl @Inject constructor(
    private val storyApi: StoryApi,
    private val storyDatabase: StoryDatabase,
) : StoryRepository {
    override suspend fun getStories(): LiveData<PagingData<StoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, storyApi),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    override suspend fun getStoriesLocation(loc: Int): Flow<BaseResult<StoryResponse, StoryResponse>> {
        return flow {
            val response = storyApi.getStoriesLocation(loc)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    emit(BaseResult.Success(body))
                }
            } else {
                val type = object : TypeToken<StoryResponse>() {}.type
                val error: StoryResponse =
                    Gson().fromJson(response.errorBody()!!.charStream(), type)
                error.code = response.code()
                emit(BaseResult.Error(error))
            }
        }
    }

    override suspend fun getStoryById(id: String): Flow<BaseResult<StoryEntity, DetailResponse>> {
        return flow {
            val response = storyApi.getStoryById(id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val data = body.story!!
                    val detailStory = StoryEntity(
                        data.id,
                        data.photoUrl,
                        data.createdAt,
                        data.name,
                        data.description,
                        data.lon,
                        data.lat
                    )
                    emit(BaseResult.Success(detailStory))
                }

            } else {
                val type = object : TypeToken<DetailResponse>() {}.type
                val error: DetailResponse =
                    Gson().fromJson(response.errorBody()!!.charStream(), type)
                error.code = response.code()
                emit(BaseResult.Error(error))
            }
        }
    }

    override suspend fun postStory(
        photo: MultipartBody.Part,
        desc: RequestBody,
        lat: Float,
        lon: Float,
    ): Flow<SimpleResult<ResponseWrapper>> {
        return flow {
            val response = storyApi.addStory(photo, desc, lat, lon)
            if (response.isSuccessful) {
                val body = response.body()!!
                emit(SimpleResult.Success(body.message))
            } else {
                val type = object : TypeToken<ResponseWrapper>() {}.type
                val error: ResponseWrapper =
                    Gson().fromJson(response.errorBody()!!.charStream(), type)
                error.code = response.code()
                emit(SimpleResult.Error(error))
            }
        }
    }
}