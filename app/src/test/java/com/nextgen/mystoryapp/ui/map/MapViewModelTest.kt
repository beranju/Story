package com.nextgen.mystoryapp.ui.map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nextgen.mystoryapp.data.story.remote.dto.StoryResponse
import com.nextgen.mystoryapp.domain.common.base.BaseResult
import com.nextgen.mystoryapp.domain.story.StoryRepository
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
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MapViewModelTest{
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val dummyData = DataDummy.generateDummyStoryResponse()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var mapUseCase: StoryUseCase
    private lateinit var mapViewModel: MapViewModel

    @Before
    fun setup(){
        mapUseCase = StoryUseCase(storyRepository)
        mapViewModel = MapViewModel(mapUseCase)
    }

    @Test
    fun `when get story success and should not null`() = runTest{
        val expected: Flow<BaseResult<StoryResponse, StoryResponse>> = flow {
            emit(BaseResult.Success(dummyData))
        }
        `when`(mapUseCase.invoke(1)).thenReturn(expected)

        val actual = mapUseCase.invoke(1)
        assertNotNull(actual)
        assertEquals(expected, actual)
    }

    @Test
    fun `when get story failed should return baseresult error`() = runTest{
        val expected: Flow<BaseResult<StoryResponse, StoryResponse>> = flow {
            storyRepository.getStoriesLocation(1).collect{error->
                if (error is BaseResult.Error){
                    emit(BaseResult.Error(error.rawResponse))
                }
            }
        }
        `when`(mapUseCase.invoke(1)).thenReturn(expected)
        val actual = mapUseCase.invoke(1)
        assertEquals(expected, actual)
    }
}