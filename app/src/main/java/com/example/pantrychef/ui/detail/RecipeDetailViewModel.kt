package com.example.pantrychef.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pantrychef.data.model.RecipeDetail
import com.example.pantrychef.data.repository.RecipeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
            val result = recipeRepository.getRecipeDetails(recipeId)
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
        viewModelScope.launch {
            val currentFavorite = _isFavorite.value
            if (currentFavorite) {
                recipeRepository.removeFavorite(recipeId)
                _isFavorite.value = false
            } else {
                recipeRepository.addFavorite(
                    com.example.pantrychef.data.model.Recipe(
                        id = recipeId,
                        name = recipeName,
                        thumbnail = thumbnail
                    )
                )
                _isFavorite.value = true
            }
        }
    }
    
    fun retry() {
        currentRecipeId?.let { loadRecipeDetail(it) }
    }
}

