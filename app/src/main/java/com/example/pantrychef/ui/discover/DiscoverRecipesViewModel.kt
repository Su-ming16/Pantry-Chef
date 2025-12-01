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
    data class SuccessWithRecommendations(
        val fullMatchRecipes: List<Recipe>,
        val recommendedRecipes: List<Recipe>
    ) : DiscoverUiState()
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
    
    private val _availableEquipment = MutableStateFlow<List<String>>(emptyList())
    val availableEquipment: StateFlow<List<String>> = _availableEquipment.asStateFlow()
    
    init {
        viewModelScope.launch {
            recipeRepository.getAllIngredients()
                .collect { ingredients ->
                    _availableIngredients.value = ingredients.map { it.name }
                }
        }
        
        viewModelScope.launch {
            recipeRepository.getAllEquipment()
                .collect { equipment ->
                    _availableEquipment.value = equipment.map { it.name }
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
    
    fun searchWithSmartMatching() {
        val ingredients = _availableIngredients.value
        android.util.Log.d("DiscoverRecipesVM", "searchWithSmartMatching: ingredients=$ingredients")
        if (ingredients.isEmpty()) {
            _uiState.value = DiscoverUiState.Empty
            return
        }
        
        if (_uiState.value is DiscoverUiState.SuccessWithRecommendations || _uiState.value is DiscoverUiState.Success) {
            android.util.Log.d("DiscoverRecipesVM", "Already have results, skipping search")
            return
        }
        
        _uiState.value = DiscoverUiState.Loading
        
        viewModelScope.launch {
            val equipment = _availableEquipment.value
            android.util.Log.d("DiscoverRecipesVM", "Searching with equipment=$equipment")
            val result = recipeRepository.searchRecipesWithSmartMatching(ingredients, equipment)
            if (result.isSuccess) {
                val (fullMatch, partialMatch) = result.getOrNull() ?: Pair(emptyList(), emptyList())
                android.util.Log.d("DiscoverRecipesVM", "Results: fullMatch=${fullMatch.size}, partialMatch=${partialMatch.size}")
                fullMatch.forEach { android.util.Log.d("DiscoverRecipesVM", "  Full: ${it.name}") }
                partialMatch.forEach { android.util.Log.d("DiscoverRecipesVM", "  Partial: ${it.name}") }
                if (fullMatch.isEmpty() && partialMatch.isEmpty()) {
                    _uiState.value = DiscoverUiState.Empty
                } else {
                    _uiState.value = DiscoverUiState.SuccessWithRecommendations(fullMatch, partialMatch)
                }
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Loading failed"
                android.util.Log.e("DiscoverRecipesVM", "Search failed: $errorMsg")
                _uiState.value = DiscoverUiState.Error(errorMsg)
            }
        }
    }
    
    fun searchWithFirstAvailableIngredient() {
        searchWithSmartMatching()
    }
}

