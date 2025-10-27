package com.tomazcuber.yourcookbok.saved.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomazcuber.yourcookbok.domain.model.Recipe
import com.tomazcuber.yourcookbok.domain.usecase.DeleteRecipeUseCase
import com.tomazcuber.yourcookbok.domain.usecase.GetSavedRecipesUseCase
import com.tomazcuber.yourcookbok.saved.screen.SavedRecipesEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedRecipesViewModel @Inject constructor(
    getSavedRecipesUseCase: GetSavedRecipesUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase
) : ViewModel() {

    val savedRecipes: StateFlow<List<Recipe>> = getSavedRecipesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onEvent(event: SavedRecipesEvent) {
        when (event) {
            is SavedRecipesEvent.OnUnsaveClick -> {
                viewModelScope.launch {
                    deleteRecipeUseCase(event.recipe)
                }
            }
            is SavedRecipesEvent.OnRecipeClick -> {
                // TODO: Handle navigation to detail screen
            }
        }
    }
}