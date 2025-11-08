package com.example.pantrychef.data.repository

import com.example.pantrychef.data.model.Ingredient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class FakeIngredientRepository : IngredientRepository {
    private val ingredientsState = MutableStateFlow<List<Ingredient>>(
        listOf(
            Ingredient(1, "Chicken"),
            Ingredient(2, "Potato"),
            Ingredient(3, "Onion")
        )
    )
    
    private val commonIngredients = listOf(
        "Chicken", "Beef", "Pork", "Fish", "Egg", "Milk", "Cheese",
        "Potato", "Onion", "Tomato", "Carrot", "Broccoli", "Spinach",
        "Rice", "Pasta", "Bread", "Flour", "Sugar", "Salt", "Pepper",
        "Garlic", "Ginger", "Olive Oil", "Butter", "Lemon", "Lime"
    )
    
    override fun getAllIngredients(): Flow<List<Ingredient>> = ingredientsState
    
    override fun searchIngredients(query: String): Flow<List<Ingredient>> = flow {
        delay(100)
        if (query.isBlank()) {
            emit(emptyList())
        } else {
            val matches = commonIngredients
                .filter { it.contains(query, ignoreCase = true) }
                .mapIndexed { index, name -> Ingredient((index + 1000).toLong(), name) }
            emit(matches)
        }
    }
    
    override suspend fun addIngredient(name: String) {
        delay(200)
        val trimmedName = name.trim()
        if (trimmedName.isNotBlank()) {
            val current = ingredientsState.value
            if (current.none { it.name.equals(trimmedName, ignoreCase = true) }) {
                val newId = (current.maxOfOrNull { it.id } ?: 0) + 1
                val newIngredient = Ingredient(newId, trimmedName)
                ingredientsState.value = current + newIngredient
            }
        }
    }
    
    override suspend fun deleteIngredient(ingredient: Ingredient) {
        delay(200)
        val current = ingredientsState.value
        ingredientsState.value = current.filter { it.id != ingredient.id }
    }
}

