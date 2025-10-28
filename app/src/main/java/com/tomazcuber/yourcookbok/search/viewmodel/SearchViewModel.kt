package com.tomazcuber.yourcookbok.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomazcuber.yourcookbok.domain.model.Recipe
import com.tomazcuber.yourcookbok.domain.model.RecipeError
import com.tomazcuber.yourcookbok.domain.usecase.DeleteRecipeUseCase
import com.tomazcuber.yourcookbok.domain.usecase.GetSavedRecipesUseCase
import com.tomazcuber.yourcookbok.domain.usecase.SaveRecipeUseCase
import com.tomazcuber.yourcookbok.search.screen.RecipeUiModel
import com.tomazcuber.yourcookbok.search.screen.SearchEvent
import com.tomazcuber.yourcookbok.search.screen.SearchUiState
import com.tomazcuber.yourcookbok.search.usecase.SearchRecipesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRecipesUseCase: SearchRecipesUseCase,
    private val getSavedRecipesUseCase: GetSavedRecipesUseCase,
    private val saveRecipeUseCase: SaveRecipeUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val savedRecipeIds = MutableStateFlow<Set<String>>(emptySet())
    private val searchResults = MutableStateFlow<List<Recipe>>(emptyList())

    init {
        observeSavedRecipes()
        observeSearchQuery()
        observeCombinedState()
    }

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.OnSearchQueryChanged -> onSearchQueryChanged(event.query)
            is SearchEvent.OnToggleSave -> onToggleSave(event.recipe)
            is SearchEvent.OnUserMessageShown -> onUserMessageShown()
            is SearchEvent.OnRecipeClick -> { /* Navigation handled by the UI */ }
            is SearchEvent.OnSearchSubmit -> triggerSearch(uiState.value.searchQuery)
        }
    }

    private fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    private fun onToggleSave(recipe: Recipe) {
        viewModelScope.launch {
            if (savedRecipeIds.value.contains(recipe.id)) {
                deleteRecipeUseCase(recipe)
            } else {
                saveRecipeUseCase(recipe)
            }
        }
    }

    private fun onUserMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }

    private fun observeSavedRecipes() {
        getSavedRecipesUseCase()
            .map { recipes -> recipes.map { it.id }.toSet() }
            .onEach { ids -> savedRecipeIds.value = ids }
            .launchIn(viewModelScope)
    }

    private fun observeSearchQuery() {
        _uiState
            .map { it.searchQuery }
            .distinctUntilChanged()
            .debounce(500L)
            .onEach { query ->
                _uiState.update { it.copy(isLoading = true) }
                triggerSearch(query)
            }
            .launchIn(viewModelScope)
    }

    private fun triggerSearch(query: String) {
        viewModelScope.launch {
            searchRecipesUseCase(query)
                .onSuccess { recipes ->
                    searchResults.value = recipes
                    _uiState.update { it.copy(isLoading = false) }
                }
                .onFailure { error ->
                    searchResults.value = emptyList()
                    val message = when (error) {
                        is RecipeError.NetworkError -> "Please check your network connection."
                        else -> "An unknown error occurred."
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            userMessage = message
                        )
                    }
                }
        }
    }

    private fun observeCombinedState() {
        searchResults.combine(savedRecipeIds) { recipes, savedIds ->
            recipes.map { recipe ->
                RecipeUiModel(recipe, savedIds.contains(recipe.id))
            }
        }.onEach { uiModels ->
            _uiState.update { it.copy(recipes = uiModels) }
        }.launchIn(viewModelScope)
    }
}