package com.nextgen.mystoryapp.data.story.remote.dto

import com.google.gson.annotations.SerializedName

data class StoryResponse(
	var code: Int,

	@field:SerializedName("listStory")
	var listStory: List<StoryItem>,

	@field:SerializedName("error")
	var error: Boolean? = null,

	@field:SerializedName("message")
	var message: String? = null,
)

