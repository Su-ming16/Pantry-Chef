package com.example.pantrychef.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pantrychef.data.repository.RecipeRepository
import com.example.pantrychef.ui.pantry.MyPantryViewModel
import com.example.pantrychef.ui.discover.DiscoverRecipesViewModel
import com.example.pantrychef.ui.detail.RecipeDetailViewModel
import com.example.pantrychef.ui.favorites.MyFavoritesViewModel

class ViewModelFactory(
    private val repository: RecipeRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPantryViewModel::class.java)) {
            return MyPantryViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(DiscoverRecipesViewModel::class.java)) {
            return DiscoverRecipesViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(RecipeDetailViewModel::class.java)) {
            return RecipeDetailViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(MyFavoritesViewModel::class.java)) {
            return MyFavoritesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}