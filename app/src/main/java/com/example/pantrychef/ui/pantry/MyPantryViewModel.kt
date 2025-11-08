package com.example.pantrychef.ui.pantry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pantrychef.data.model.Ingredient
import com.example.pantrychef.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MyPantryViewModel(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _ingredients = MutableStateFlow<List<Ingredient>>(emptyList())
    val ingredients: StateFlow<List<Ingredient>> = _ingredients.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllIngredients()
                .collect { ingredientList -> _ingredients.value = ingredientList }
        }
    }

    private val _searchSuggestions = MutableStateFlow<List<Ingredient>>(emptyList())
    val searchSuggestions: StateFlow<List<Ingredient>> = _searchSuggestions.asStateFlow()

    // THIS FUNCTION IS NOW FULLY IMPLEMENTED
    fun searchIngredients(query: String) {
        viewModelScope.launch {
            // No longer empty, it now correctly calls the repository
            repository.searchIngredients(query)
                .collect { suggestions ->
                    _searchSuggestions.value = suggestions
                }
        }
    }

    fun addIngredient(name: String) {
        viewModelScope.launch {
            if (name.isNotBlank()) {
                repository.addIngredient(Ingredient(name = name.trim()))
            }
        }
    }

    fun deleteIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            repository.deleteIngredient(ingredient)
        }
    }
}