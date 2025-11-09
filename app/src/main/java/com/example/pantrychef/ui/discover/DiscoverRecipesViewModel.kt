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
    
    private val _availableIngredients = MutableStateFlow<List<String>>(emptyList())
    val availableIngredients: StateFlow<List<String>> = _availableIngredients.asStateFlow()
    
    init {
        viewModelScope.launch {
            recipeRepository.getAllIngredients()
                .collect { ingredients ->
                    _availableIngredients.value = ingredients.map { it.name }
                }
        }
    }
    
    fun searchRecipes(ingredient: String) {
        if (ingredient.isBlank()) {
            _uiState.value = DiscoverUiState.Empty
            return
        }
        
        _uiState.value = DiscoverUiState.Loading
        
        viewModelScope.launch {
            val result = recipeRepository.searchRecipesByIngredient(ingredient)
            if (result.isSuccess) {
                val recipes = result.getOrNull() ?: emptyList()
                if (recipes.isEmpty()) {
                    _uiState.value = DiscoverUiState.Empty
                } else {
                    _uiState.value = DiscoverUiState.Success(recipes)
                }
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Loading failed"
                _uiState.value = DiscoverUiState.Error(errorMsg)
            }
        }
    }
    
    fun retry(ingredient: String) {
        searchRecipes(ingredient)
    }
    
    fun searchWithFirstAvailableIngredient() {
        val firstIngredient = _availableIngredients.value.firstOrNull()
        if (firstIngredient != null) {
            searchRecipes(firstIngredient)
        } else {
            _uiState.value = DiscoverUiState.Empty
        }
    }
}

