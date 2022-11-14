package com.nextgen.mystoryapp.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.nextgen.mystoryapp.data.story.remote.dto.StoryItem
import com.nextgen.mystoryapp.data.story.remote.paging.StoryPagingSource
import com.nextgen.mystoryapp.domain.story.StoryRepository
import com.nextgen.mystoryapp.domain.story.usecase.DetailStoryUseCase
import com.nextgen.mystoryapp.domain.story.usecase.StoryUseCase
import com.nextgen.mystoryapp.infra.utils.DataDummy
import com.nextgen.mystoryapp.infra.utils.MainDispatcherRule
import com.nextgen.mystoryapp.infra.utils.getOrAwaitValue
import com.nextgen.mystoryapp.ui.adapter.ListStoriesAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.test.*
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest{

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    @Mock
    private lateinit var storyUseCase: StoryUseCase

    @Test
    fun `when get Story should not null and return success`() = runTest {
        val dataStory = DataDummy.generateDummyListStoryItem()
        val data: PagingData<StoryItem> = StoryPaggingSource.snapshot(dataStory)

        storyUseCase = StoryUseCase(storyRepository)
        val expected = MutableLiveData<PagingData<StoryItem>>()
        expected.value = data
        `when`(storyUseCase.getStory()).thenReturn(expected)

        val homeViewModel = HomeViewModel(storyUseCase)
        val actual: PagingData<StoryItem> = homeViewModel.story.getOrAwaitValue()

        //cek data dengan AsyncPagingDataDiffer
        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoriesAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actual)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dataStory, differ.snapshot())
        Assert.assertEquals(dataStory.size, differ.snapshot().size)
        Assert.assertEquals(dataStory[0].name, differ.snapshot()[0]?.name)
    }
}


class StoryPaggingSource: PagingSource<Int, LiveData<List<StoryItem>>>(){

    companion object{
        fun snapshot(item: List<StoryItem>): PagingData<StoryItem>{
            return PagingData.from(item)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoryItem>>>): Int? {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoryItem>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }

}

val noopListUpdateCallback = object : ListUpdateCallback{
    override fun onInserted(position: Int, count: Int) {}

    override fun onRemoved(position: Int, count: Int) {}

    override fun onMoved(fromPosition: Int, toPosition: Int) {}

    override fun onChanged(position: Int, count: Int, payload: Any?) {}

}