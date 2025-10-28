package com.tomazcuber.yourcookbok.detail.screen

sealed class RecipeDetailEvent {
    data object OnToggleSave : RecipeDetailEvent()
    data object OnUserMessageShown : RecipeDetailEvent()
}