package com.example.pantrychef.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pantrychef.data.repository.RecipeRepository
import com.example.pantrychef.ui.pantry.MyPantryViewModel
// Import other ViewModels here as you create/need them
// import com.example.pantrychef.ui.discover.DiscoverRecipesViewModel
// import com.example.pantrychef.ui.detail.RecipeDetailViewModel
// import com.example.pantrychef.ui.favorites.MyFavoritesViewModel

/**
 * A factory for creating ANY ViewModel that requires our unified RecipeRepository.
 * This is the single source of truth for ViewModel creation.
 */
class ViewModelFactory(
    // CHANGED: The constructor now takes a single, unified repository.
    private val repository: RecipeRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // We use a 'when' statement to check which ViewModel is being requested.
        return when {
            // If the Fragment asks for a MyPantryViewModel...
            modelClass.isAssignableFrom(MyPantryViewModel::class.java) -> {
                // ...create one, giving it the repository it needs.
                MyPantryViewModel(repository) as T
            }

            // --- Add other ViewModels here as they are created by Role C ---
            /*
            modelClass.isAssignableFrom(DiscoverRecipesViewModel::class.java) -> {
                DiscoverRecipesViewModel(repository) as T
            }
            modelClass.isAssignableFrom(RecipeDetailViewModel::class.java) -> {
                RecipeDetailViewModel(repository) as T
            }
            modelClass.isAssignableFrom(MyFavoritesViewModel::class.java) -> {
                MyFavoritesViewModel(repository) as T
            }
            */

            // If the requested ViewModel is not in our list, throw an error.
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}