package com.example.pantrychef.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pantrychef.data.model.RecipeDetail
import com.example.pantrychef.data.repository.RecipeRepository
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
    
    fun loadRecipeDetail(recipeId: String) {
        currentRecipeId = recipeId
        _uiState.value = DetailUiState.Loading
        
        viewModelScope.launch {
            recipeRepository.isFavorite(recipeId)
                .collect { favorite ->
                    _isFavorite.value = favorite
                }
        }
        
        viewModelScope.launch {
            val result = recipeRepository.getRecipeDetails(recipeId)
            if (result.isSuccess) {
                val detail = result.getOrNull()
                if (detail != null) {
                    _uiState.value = DetailUiState.Success(detail)
                } else {
                    _uiState.value = DetailUiState.Error("Recipe not found")
                }
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Loading failed"
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

