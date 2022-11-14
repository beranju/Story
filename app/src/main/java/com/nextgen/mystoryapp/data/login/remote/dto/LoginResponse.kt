package com.nextgen.mystoryapp.data.login.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    var code: Int,

    @field:SerializedName("loginResult")
    var loginResult: LoginResult? = null,

    @field:SerializedName("error")
    var error: Boolean? = null,

    @field:SerializedName("message")
    var message: String? = null,
)

data class LoginResult(

    @field:SerializedName("name")
    var name: String? = null,

    @field:SerializedName("userId")
    var userId: String? = null,

    @field:SerializedName("token")
    var token: String? = null,
)
