package com.nextgen.mystoryapp.data.login.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email") var email: String,
    @SerializedName("password") var password: String,
)