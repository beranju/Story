package com.nextgen.mystoryapp.data.story.remote.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoryItem(
    @field:SerializedName("photoUrl")
    var photoUrl: String? = null,

    @field:SerializedName("createdAt")
    var createdAt: String? = null,

    @field:SerializedName("name")
    var name: String? = null,

    @field:SerializedName("description")
    var description: String? = null,

    @field:SerializedName("lon")
    var lon: Float? = null,

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("lat")
    var lat: Float? = null,
) : Parcelable
