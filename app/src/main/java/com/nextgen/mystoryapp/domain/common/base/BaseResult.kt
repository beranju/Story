package com.nextgen.mystoryapp.domain.common.base


sealed class BaseResult<out T : Any?, out U : Any?> {
    data class Success<T : Any>(val data: T) : BaseResult<T, Nothing>()
    data class Error<U : Any>(val rawResponse: U) : BaseResult<Nothing, U>()
}

sealed class SimpleResult<out T : Any?> {
    data class Success(val message: String) : SimpleResult<Nothing>()
    data class Error<T : Any>(val rawResponse: T) : SimpleResult<T>()
}
