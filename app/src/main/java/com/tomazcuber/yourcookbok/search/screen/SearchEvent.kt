package com.tomazcuber.yourcookbok.search.screen

import com.tomazcuber.yourcookbok.domain.model.Recipe

sealed class SearchEvent {
    data class OnSearchQueryChanged(val query: String) : SearchEvent()
    data class OnToggleSave(val recipe: Recipe) : SearchEvent()
    data class OnRecipeClick(val recipe: Recipe) : SearchEvent()
    object OnSearchSubmit : SearchEvent()
    object OnUserMessageShown : SearchEvent()
}