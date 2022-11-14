package com.nextgen.mystoryapp.data.story.local.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import com.nextgen.mystoryapp.data.story.local.dao.StoryDao
import com.nextgen.mystoryapp.data.story.mediator.RemoteKeys
import com.nextgen.mystoryapp.data.story.mediator.RemoteKeysDao
import com.nextgen.mystoryapp.domain.story.entity.StoryEntity

@Database(
    entities = [StoryEntity::class, RemoteKeys::class],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2, spec = StoryDatabase.MyAutoMigration::class)
    ],
    exportSchema = true
)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    class MyAutoMigration : AutoMigrationSpec
}