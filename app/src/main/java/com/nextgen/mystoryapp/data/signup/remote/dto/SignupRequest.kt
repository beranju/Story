package com.nextgen.mystoryapp.data.signup.remote.dto

import com.google.gson.annotations.SerializedName

data class SignupRequest(
    @SerializedName("name") var name: String,
    @SerializedName("email") var email: String,
    @SerializedName("password") var password: String,
)