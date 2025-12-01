package com.example.pantrychef.ui.camera

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SharedRecognitionViewModel : ViewModel() {
    private val _categories = MutableStateFlow<List<IngredientCategory>>(emptyList())
    val categories: StateFlow<List<IngredientCategory>> = _categories.asStateFlow()
    
    fun setCategories(newCategories: List<IngredientCategory>) {
        _categories.value = newCategories
    }
    
    fun clear() {
        _categories.value = emptyList()
    }
}

