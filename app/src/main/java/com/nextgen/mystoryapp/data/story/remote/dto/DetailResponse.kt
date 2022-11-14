package com.nextgen.mystoryapp.data.story.remote.dto

import com.google.gson.annotations.SerializedName

data class DetailResponse(
    var code: Int,

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("story")
    val story: StoryItem? = null,
)
