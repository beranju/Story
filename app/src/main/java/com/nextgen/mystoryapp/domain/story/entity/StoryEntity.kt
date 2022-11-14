package com.nextgen.mystoryapp.domain.story.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "story")
@Parcelize
data class StoryEntity(
    @PrimaryKey
    val id: String,

    var photoUrl: String? = null,

    var createdAt: String? = null,

    var name: String? = null,

    var description: String? = null,

    var lon: Float? = null,

    var lat: Float? = null,
) : Parcelable
