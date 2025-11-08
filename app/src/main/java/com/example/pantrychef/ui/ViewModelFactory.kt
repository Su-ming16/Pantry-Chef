package com.example.pantrychef.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pantrychef.data.repository.FakeIngredientRepository
import com.example.pantrychef.data.repository.FakeRecipeRepository
import com.example.pantrychef.data.repository.IngredientRepository
import com.example.pantrychef.data.repository.RecipeRepository
import com.example.pantrychef.ui.detail.RecipeDetailViewModel
import com.example.pantrychef.ui.discover.DiscoverRecipesViewModel
import com.example.pantrychef.ui.favorites.MyFavoritesViewModel
import com.example.pantrychef.ui.pantry.MyPantryViewModel

class ViewModelFactory(
    private val ingredientRepository: IngredientRepository = FakeIngredientRepository(),
    private val recipeRepository: RecipeRepository = FakeRecipeRepository()
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MyPantryViewModel::class.java) -> {
                MyPantryViewModel(ingredientRepository) as T
            }
            modelClass.isAssignableFrom(DiscoverRecipesViewModel::class.java) -> {
                DiscoverRecipesViewModel(recipeRepository) as T
            }
            modelClass.isAssignableFrom(RecipeDetailViewModel::class.java) -> {
                RecipeDetailViewModel(recipeRepository) as T
            }
            modelClass.isAssignableFrom(MyFavoritesViewModel::class.java) -> {
                MyFavoritesViewModel(recipeRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

