package com.tomazcuber.yourcookbok.domain.model

sealed class RecipeError : Throwable() {
    data class NetworkError(override val cause: Throwable) : RecipeError()
    data class DatabaseError(override val cause: Throwable) : RecipeError()
    data object RecipeNotFound : RecipeError() {
        private fun readResolve(): Any = RecipeNotFound
    }
}