package com.example.pantrychef.ui.pantry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pantrychef.data.model.Ingredient
import com.example.pantrychef.data.repository.IngredientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class MyPantryViewModel(
    private val ingredientRepository: IngredientRepository
) : ViewModel() {
    
    private val _ingredients = MutableStateFlow<List<Ingredient>>(emptyList())
    val ingredients: StateFlow<List<Ingredient>> = _ingredients.asStateFlow()
    
    init {
        viewModelScope.launch {
            ingredientRepository.getAllIngredients()
                .collect { _ingredients.value = it }
        }
    }
    
    private val _searchSuggestions = MutableStateFlow<List<Ingredient>>(emptyList())
    val searchSuggestions: StateFlow<List<Ingredient>> = _searchSuggestions.asStateFlow()
    
    fun searchIngredients(query: String) {
        viewModelScope.launch {
            ingredientRepository.searchIngredients(query)
                .collect { suggestions ->
                    _searchSuggestions.value = suggestions
                }
        }
    }
    
    fun addIngredient(name: String) {
        viewModelScope.launch {
            ingredientRepository.addIngredient(name)
        }
    }
    
    fun deleteIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            ingredientRepository.deleteIngredient(ingredient)
        }
    }
}

