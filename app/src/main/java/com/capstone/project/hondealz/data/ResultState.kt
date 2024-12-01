package com.capstone.project.hondealz.data

sealed class ResultState<out R> {
    data class Success<out T>(val data: T) : ResultState<T>()
    data class Error(val statusCode: Int, val error: String) : ResultState<Nothing>()
    data object Loading : ResultState<Nothing>()
}