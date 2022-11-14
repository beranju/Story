package com.nextgen.mystoryapp.data.common.util

import com.google.gson.annotations.SerializedName

data class ResponseWrapper(
    var code: Int,
    @SerializedName("error") var error: Boolean,
    @SerializedName("message") var message: String,
)