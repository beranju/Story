package com.nextgen.mystoryapp.data.story.mediator

import androidx.paging.*
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nextgen.mystoryapp.data.common.util.ResponseWrapper
import com.nextgen.mystoryapp.data.story.local.database.StoryDatabase
import com.nextgen.mystoryapp.data.story.remote.api.StoryApi
import com.nextgen.mystoryapp.data.story.remote.dto.DetailResponse
import com.nextgen.mystoryapp.data.story.remote.dto.StoryItem
import com.nextgen.mystoryapp.data.story.remote.dto.StoryResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
class StoryRemoteMediatorTest{
    //fake
    private var mockApi: StoryApi = FakeApiService()
    private var mockDb: StoryDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        StoryDatabase::class.java
    ).allowMainThreadQueries().build()

    @Test
    fun refreshReturnSuccessResultWhenMoreDataIsPresent() = runTest {
        val remoteMediator = StoryRemoteMediator(
            mockDb,mockApi
        )

        val pagingState = PagingState<Int, StoryItem>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )

        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @After
    fun tearDown(){
        mockDb.clearAllTables()
    }

}

class FakeApiService : StoryApi {
    override suspend fun getStories(page: Int, size: Int): List<StoryItem> {
        val item: MutableList<StoryItem> = arrayListOf()
        for (i in 0..100){
            val story = StoryItem(
                id = "$i"
            )
            item.add(story)
        }
        return item.subList((page - 1) * size, (page-1)* size + size)
    }

    override suspend fun getStoriesLocation(location: Int): Response<StoryResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getStoryById(id: String): Response<DetailResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun addStory(
        photo: MultipartBody.Part,
        description: RequestBody,
        lat: Float,
        lon: Float,
    ): Response<ResponseWrapper> {
        TODO("Not yet implemented")
    }

}
