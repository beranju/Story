package com.nextgen.mystoryapp.data.story.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.nextgen.mystoryapp.data.story.remote.api.StoryApi
import com.nextgen.mystoryapp.data.story.remote.dto.StoryItem

class StoryPagingSource(private val api: StoryApi) : PagingSource<Int, StoryItem>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
        const val LOCATION = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = api.getStories(position, params.loadSize).listStory
            LoadResult.Page(
                data = responseData,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.isNullOrEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, StoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}