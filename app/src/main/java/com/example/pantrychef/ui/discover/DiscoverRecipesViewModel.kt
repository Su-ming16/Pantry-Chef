package com.example.pantrychef.ui.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pantrychef.data.model.Recipe
import com.example.pantrychef.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DiscoverUiState {
    object Loading : DiscoverUiState()
    data class Success(val recipes: List<Recipe>) : DiscoverUiState()
    data class Error(val message: String) : DiscoverUiState()
    object Empty : DiscoverUiState()
}

class DiscoverRecipesViewModel(
    private val recipeRepository: RecipeRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<DiscoverUiState>(DiscoverUiState.Empty)
    val uiState: StateFlow<DiscoverUiState> = _uiState.asStateFlow()
    
    fun searchRecipes(ingredient: String) {
        if (ingredient.isBlank()) {
            _uiState.value = DiscoverUiState.Empty
            return
        }
        
        _uiState.value = DiscoverUiState.Loading
        
        viewModelScope.launch {
            recipeRepository.searchRecipesByIngredient(ingredient)
                .collect { result ->
                    result.getOrNull()?.let { recipes ->
                        _uiState.value = if (recipes.isEmpty()) {
                            DiscoverUiState.Empty
                        } else {
                            DiscoverUiState.Success(recipes)
                        }
                    } ?: run {
                        val error = result.exceptionOrNull()
                        _uiState.value = DiscoverUiState.Error(error?.message ?: "Loading failed")
                    }
                }
        }
    }
    
    fun retry(ingredient: String) {
        searchRecipes(ingredient)
    }
}

