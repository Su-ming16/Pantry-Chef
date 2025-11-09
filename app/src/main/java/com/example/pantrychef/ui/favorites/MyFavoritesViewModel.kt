package com.example.pantrychef.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pantrychef.data.model.Recipe
import com.example.pantrychef.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class MyFavoritesViewModel(
    private val recipeRepository: RecipeRepository
) : ViewModel() {
    
    private val _favorites = MutableStateFlow<List<Recipe>>(emptyList())
    val favorites: StateFlow<List<Recipe>> = _favorites.asStateFlow()
    
    init {
        viewModelScope.launch {
            recipeRepository.getAllFavorites()
                .collect { favoriteRecipes ->
                    _favorites.value = favoriteRecipes.map { favorite ->
                        Recipe(
                            id = favorite.id,
                            name = favorite.name,
                            thumbnail = favorite.imageUrl
                        )
                    }
                }
        }
    }
}

