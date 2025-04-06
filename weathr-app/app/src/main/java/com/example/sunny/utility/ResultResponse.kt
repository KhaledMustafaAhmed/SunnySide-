package com.example.sunny.utility

sealed class ResultResponse<out T>{
    data class Success<out T>(val value: T): ResultResponse<T>()

    data class Failure(val message: String): ResultResponse<Nothing>()

    data object Loading: ResultResponse<Nothing>()
}