package com.nextgen.mystoryapp.ui.camera

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nextgen.mystoryapp.data.common.util.ResponseWrapper
import com.nextgen.mystoryapp.domain.common.base.SimpleResult
import com.nextgen.mystoryapp.domain.story.StoryRepository
import com.nextgen.mystoryapp.domain.story.usecase.AddStoryUseCase
import com.nextgen.mystoryapp.infra.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.io.File


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AddStoryViewModelTest{
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    @Mock
    private lateinit var addStoryUseCase: AddStoryUseCase
    private lateinit var addStoryViewModel: AddStoryViewModel


    @Before
    fun setup(){
        addStoryViewModel = AddStoryViewModel(addStoryUseCase)
    }

    @Test
    fun `when post story success should return success response`() = runTest{
        val description = "test".toRequestBody("text/plain".toMediaType())
        val file = Mockito.mock(File::class.java)
        val requestImageFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "test",
            file.name,
            requestImageFile
        )

        val expected: Flow<SimpleResult<ResponseWrapper>> = flow {
            emit(SimpleResult.Success("Success"))
        }

        `when`(addStoryUseCase.invoke(imageMultipart, description, 0F, 0F)).thenReturn(expected)
        val actual = addStoryUseCase.invoke(imageMultipart, description, 0F, 0F)
        verify(addStoryUseCase).invoke(imageMultipart, description, 0F, 0F)
        assertEquals(expected, actual)
    }
}