package com.nextgen.mystoryapp.data.story.remote.api

import com.nextgen.mystoryapp.data.common.util.ResponseWrapper
import com.nextgen.mystoryapp.data.story.remote.dto.DetailResponse
import com.nextgen.mystoryapp.data.story.remote.dto.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*


interface StoryApi {
    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): StoryResponse

    @GET("stories")
    suspend fun getStoriesLocation(
        @Query("location") location: Int,
    ): Response<StoryResponse>

    @GET("stories/{id}")
    suspend fun getStoryById(@Path("id") id: String): Response<DetailResponse>

    @POST("stories")
    @Multipart
    suspend fun addStory(
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Float,
        @Part("lon") lon: Float,
    ): Response<ResponseWrapper>
}