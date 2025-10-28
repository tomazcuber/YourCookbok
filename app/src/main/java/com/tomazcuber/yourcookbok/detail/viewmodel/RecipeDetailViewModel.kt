package com.tomazcuber.yourcookbok.detail.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomazcuber.yourcookbok.detail.screen.RecipeDetailEvent
import com.tomazcuber.yourcookbok.detail.screen.RecipeDetailUiState
import com.tomazcuber.yourcookbok.detail.usecase.GetRecipeDetailsUseCase
import com.tomazcuber.yourcookbok.domain.model.Recipe
import com.tomazcuber.yourcookbok.domain.model.RecipeError
import com.tomazcuber.yourcookbok.domain.usecase.DeleteRecipeUseCase
import com.tomazcuber.yourcookbok.domain.usecase.IsRecipeSavedUseCase
import com.tomazcuber.yourcookbok.domain.usecase.SaveRecipeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getRecipeDetailsUseCase: GetRecipeDetailsUseCase,
    private val isRecipeSavedUseCase: IsRecipeSavedUseCase,
    private val saveRecipeUseCase: SaveRecipeUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    private val recipeId: String = savedStateHandle.get<String>("recipeId")!!

    init {
        fetchRecipeDetails()
        observeSavedStatus()
    }

    fun onEvent(event: RecipeDetailEvent) {
        when (event) {
            is RecipeDetailEvent.OnToggleSave -> onToggleSave()
            is RecipeDetailEvent.OnUserMessageShown -> onUserMessageShown()
        }
    }

    private fun onToggleSave() {
        viewModelScope.launch {
            val recipe = _uiState.value.recipe ?: return@launch
            if (_uiState.value.isSaved) {
                deleteRecipeUseCase(recipe)
            } else {
                saveRecipeUseCase(recipe)
            }
        }
    }

    private fun onUserMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }

    private fun fetchRecipeDetails() {
        viewModelScope.launch {
            getRecipeDetailsUseCase(recipeId)
                .onSuccess { recipe ->
                    _uiState.update {
                        it.copy(
                            recipe = recipe,
                            isLoading = false,
                            numberedInstructions = splitRecipeInstructionsIntoNumberedList(recipe.instructions)
                        )
                    }
                }
                .onFailure { error ->
                    val message = when (error) {
                        is RecipeError.RecipeNotFound -> "Recipe not found."
                        is RecipeError.NetworkError -> "Please check your network connection."
                        else -> "An unknown error occurred."
                    }
                    _uiState.update { it.copy(userMessage = message, isLoading = false) }
                }
        }
    }

    private fun observeSavedStatus() {
        isRecipeSavedUseCase(recipeId)
            .onEach { isSaved ->
                _uiState.update { it.copy(isSaved = isSaved) }
            }
            .launchIn(viewModelScope)
    }

    private fun splitRecipeInstructionsIntoNumberedList(recipeInstructions: String) : List<String> {
        return recipeInstructions
            .split("\r\n")
            .filter { it.isNotBlank() }
            .mapIndexed { index, instruction -> "${index + 1}. $instruction" }
    }
}