package com.nextgen.mystoryapp.ui.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nextgen.mystoryapp.data.story.remote.dto.DetailResponse
import com.nextgen.mystoryapp.domain.common.base.BaseResult
import com.nextgen.mystoryapp.domain.story.StoryRepository
import com.nextgen.mystoryapp.domain.story.entity.StoryEntity
import com.nextgen.mystoryapp.domain.story.usecase.DetailStoryUseCase
import com.nextgen.mystoryapp.domain.story.usecase.StoryUseCase
import com.nextgen.mystoryapp.infra.utils.DataDummy
import com.nextgen.mystoryapp.infra.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class DetailViewModelTest{

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val data = DataDummy.generateDummyStoryEntity()

    private lateinit var detailViewModel: DetailViewModel
    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var detailStoryUseCase: DetailStoryUseCase


    @Before
    fun setUp(){
        detailStoryUseCase = DetailStoryUseCase(storyRepository)
        detailViewModel = DetailViewModel(detailStoryUseCase)
    }

    @Test
    fun `should success when get detail story and not null`() = runTest {
        val expected: Flow<BaseResult<StoryEntity, DetailResponse>> = flow{
            emit(BaseResult.Success(data))
        }
        `when`(detailStoryUseCase.invoke("1")).thenReturn(expected)

        val actual = detailStoryUseCase.invoke("1")
        assertNotNull(actual)
        assertEquals(expected, actual)
    }

    @Test
    fun `error get detail and shoul return error`() = runTest{
        val expected: Flow<BaseResult<StoryEntity, DetailResponse>> = flow {
            detailStoryUseCase.invoke("1").collect{error->
                if (error is BaseResult.Error){
                    emit(BaseResult.Error(error.rawResponse))
                }
            }
        }
        `when`(detailStoryUseCase.invoke("1")).thenReturn(expected)
        val actual = detailStoryUseCase.invoke("1")
        assertEquals(expected, actual)
    }

}