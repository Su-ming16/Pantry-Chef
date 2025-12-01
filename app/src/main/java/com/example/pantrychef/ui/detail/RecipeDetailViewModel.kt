package com.example.pantrychef.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pantrychef.data.model.RecipeDetail
import com.example.pantrychef.data.repository.RecipeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Success(val detail: RecipeDetail) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}

class RecipeDetailViewModel(
    private val recipeRepository: RecipeRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()
    
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()
    
    private var currentRecipeId: String? = null
    private var favoriteJob: Job? = null
    
    private val _availableIngredients = MutableStateFlow<List<String>>(emptyList())
    
    init {
        viewModelScope.launch {
            recipeRepository.getAllIngredients()
                .collect { ingredients ->
                    _availableIngredients.value = ingredients.map { it.name }
                }
        }
    }
    
    fun loadRecipeDetail(recipeId: String) {
        android.util.Log.d("RecipeDetailViewModel", "loadRecipeDetail called with recipeId=$recipeId, current=$currentRecipeId")
        if (recipeId.isBlank()) {
            _uiState.value = DetailUiState.Error("Invalid recipe ID")
            return
        }
        
        currentRecipeId = recipeId
        _uiState.value = DetailUiState.Loading
        _isFavorite.value = false
        
        favoriteJob?.cancel()
        favoriteJob = viewModelScope.launch {
            try {
                recipeRepository.isFavorite(recipeId)
                    .collect { favorite ->
                        _isFavorite.value = favorite
                    }
            } catch (e: Exception) {
                _isFavorite.value = false
            }
        }
        
        viewModelScope.launch {
            val availableIngredients = _availableIngredients.value
            android.util.Log.d("RecipeDetailViewModel", "Loading detail with availableIngredients: $availableIngredients (size=${availableIngredients.size})")
            
            if (availableIngredients.isEmpty()) {
                kotlinx.coroutines.delay(100)
                val retryIngredients = _availableIngredients.value
                android.util.Log.d("RecipeDetailViewModel", "Retry after delay: $retryIngredients (size=${retryIngredients.size})")
            }
            
            val finalIngredients = _availableIngredients.value
            val result = recipeRepository.getRecipeDetails(recipeId, finalIngredients)
            android.util.Log.d("RecipeDetailViewModel", "API result for $recipeId: isSuccess=${result.isSuccess}")
            if (result.isSuccess) {
                val detail = result.getOrNull()
                if (detail != null) {
                    android.util.Log.d("RecipeDetailViewModel", "Loaded detail: name=${detail.name}, id=${detail.id}")
                    _uiState.value = DetailUiState.Success(detail)
                } else {
                    android.util.Log.d("RecipeDetailViewModel", "Detail is null for recipeId=$recipeId")
                    _uiState.value = DetailUiState.Error("Recipe not found")
                }
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Loading failed"
                android.util.Log.e("RecipeDetailViewModel", "Error loading recipe: $errorMsg")
                _uiState.value = DetailUiState.Error(errorMsg)
            }
        }
    }
    
    fun toggleFavorite(recipeId: String, recipeName: String, thumbnail: String?) {
        android.util.Log.d("RecipeDetailViewModel", "toggleFavorite called: id=$recipeId, name=$recipeName")
        viewModelScope.launch {
            try {
                val currentFavorite = _isFavorite.value
                android.util.Log.d("RecipeDetailViewModel", "Current favorite state: $currentFavorite")
                
                val newFavoriteState = !currentFavorite
                _isFavorite.value = newFavoriteState
                
                if (currentFavorite) {
                    recipeRepository.removeFavorite(recipeId)
                    android.util.Log.d("RecipeDetailViewModel", "Removed favorite: $recipeId")
                } else {
                    val recipe = com.example.pantrychef.data.model.Recipe(
                        id = recipeId,
                        name = recipeName,
                        thumbnail = thumbnail
                    )
                    recipeRepository.addFavorite(recipe)
                    android.util.Log.d("RecipeDetailViewModel", "Added favorite: $recipeId")
                }
                
                val updatedFavorite = recipeRepository.isFavorite(recipeId).first()
                android.util.Log.d("RecipeDetailViewModel", "Updated favorite state from DB: $updatedFavorite")
                if (updatedFavorite != newFavoriteState) {
                    _isFavorite.value = updatedFavorite
                }
            } catch (e: Exception) {
                android.util.Log.e("RecipeDetailViewModel", "Error toggling favorite", e)
                e.printStackTrace()
                val actualState = try {
                    recipeRepository.isFavorite(recipeId).first()
                } catch (ex: Exception) {
                    false
                }
                _isFavorite.value = actualState
            }
        }
    }
    
    fun retry() {
        currentRecipeId?.let { loadRecipeDetail(it) }
    }
}

