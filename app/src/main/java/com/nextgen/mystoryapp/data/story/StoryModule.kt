package com.nextgen.mystoryapp.data.story

import android.content.Context
import androidx.room.Room
import com.nextgen.mystoryapp.data.common.module.NetworkModule
import com.nextgen.mystoryapp.data.story.local.dao.StoryDao
import com.nextgen.mystoryapp.data.story.local.database.StoryDatabase
import com.nextgen.mystoryapp.data.story.mediator.RemoteKeysDao
import com.nextgen.mystoryapp.data.story.remote.api.StoryApi
import com.nextgen.mystoryapp.data.story.repository.StoryRepositoryImpl
import com.nextgen.mystoryapp.domain.story.StoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
class StoryModule {

    @Singleton
    @Provides
    fun provideStoryApi(retrofit: Retrofit): StoryApi {
        return retrofit.create(StoryApi::class.java)
    }

    @Singleton
    @Provides
    fun providesStoryRepository(storyApi: StoryApi, storyDatabase: StoryDatabase): StoryRepository {
        return StoryRepositoryImpl(storyApi, storyDatabase)
    }


    @Provides
    fun providesStoryDao(database: StoryDatabase): StoryDao {
        return database.storyDao()
    }

    @Provides
    fun providesRemoteKeysDao(database: StoryDatabase): RemoteKeysDao {
        return database.remoteKeysDao()
    }


    @Singleton
    @Provides
    fun providesStoryDatabase(@ApplicationContext app: Context): StoryDatabase {
        return Room.databaseBuilder(
            app,
            StoryDatabase::class.java,
            "storyapp_database"
        )
            .build()
    }


}
