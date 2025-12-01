package com.example.pantrychef.ui.camera

import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

data class IngredientCategory(
    val categoryName: String,
    val suggestions: List<String>,
    var isExpanded: Boolean = true
)

object IngredientRecognizer {

    private val labeler by lazy {
        ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
    }

    private val foodKeywords = setOf(
        "chicken", "beef", "pork", "fish", "salmon", "tuna", "shrimp", "meat",
        "rice", "pasta", "noodle", "bread", "flour", "cereal",
        "tomato", "potato", "onion", "garlic", "carrot", "broccoli", "lettuce",
        "spinach", "cabbage", "pepper", "mushroom", "corn", "pea",
        "apple", "banana", "orange", "lemon", "strawberry", "grape",
        "milk", "cheese", "butter", "yogurt", "cream", "egg",
        "oil", "sauce", "spice", "herb", "salt", "sugar",
        "bean", "tofu", "soy", "vegetable", "fruit", "food", "ingredient", "seafood", "dairy", "produce"
    )

    fun recognizeIngredients(imageProxy: ImageProxy, callback: (List<String>) -> Unit) {
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            callback(emptyList())
            return
        }

        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        
        labeler.process(image)
            .addOnSuccessListener { labels ->
                val ingredients = mutableSetOf<String>()
                
                for (label in labels) {
                    val text = label.text.lowercase()
                    val confidence = label.confidence
                    
                    android.util.Log.d("IngredientRecognizer", "Detected: $text (confidence: ${confidence * 100}%)")
                    
                    if (confidence > 0.6f) {
                        if (foodKeywords.any { keyword -> text.contains(keyword) || keyword.contains(text) }) {
                            ingredients.add(label.text.replaceFirstChar { it.uppercaseChar() })
                        }
                        
                        for (keyword in foodKeywords) {
                            if (text.contains(keyword)) {
                                ingredients.add(keyword.replaceFirstChar { it.uppercaseChar() })
                            }
                        }
                    }
                }
                
                android.util.Log.d("IngredientRecognizer", "Final ingredients: $ingredients")
                callback(ingredients.toList().take(10))
            }
            .addOnFailureListener { e ->
                android.util.Log.e("IngredientRecognizer", "Recognition failed", e)
                callback(emptyList())
            }
    }
    
    fun recognizeWithSuggestionsFromProxy(imageProxy: ImageProxy, callback: (List<IngredientCategory>) -> Unit) {
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            callback(emptyList())
            return
        }

        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        recognizeWithSuggestions(image, callback)
    }

    fun recognizeIngredientsFromImage(image: InputImage, callback: (List<String>) -> Unit) {
        labeler.process(image)
            .addOnSuccessListener { labels ->
                val ingredients = mutableSetOf<String>()
                
                for (label in labels) {
                    val text = label.text.lowercase()
                    val confidence = label.confidence
                    
                    android.util.Log.d("IngredientRecognizer", "Detected: $text (confidence: ${confidence * 100}%)")
                    
                    if (confidence > 0.6f) {
                        if (foodKeywords.any { keyword -> text.contains(keyword) || keyword.contains(text) }) {
                            ingredients.add(label.text.replaceFirstChar { it.uppercaseChar() })
                        }
                        
                        for (keyword in foodKeywords) {
                            if (text.contains(keyword)) {
                                ingredients.add(keyword.replaceFirstChar { it.uppercaseChar() })
                            }
                        }
                    }
                }
                
                android.util.Log.d("IngredientRecognizer", "Final ingredients: $ingredients")
                callback(ingredients.toList().take(10))
            }
            .addOnFailureListener { e ->
                android.util.Log.e("IngredientRecognizer", "Recognition failed", e)
                callback(emptyList())
            }
    }
    
    fun recognizeWithSuggestions(image: InputImage, callback: (List<IngredientCategory>) -> Unit) {
        labeler.process(image)
            .addOnSuccessListener { labels ->
                val categories = mutableListOf<IngredientCategory>()
                val processedCategories = mutableSetOf<String>()
                
                for (label in labels) {
                    val text = label.text.lowercase()
                    val confidence = label.confidence
                    
                    android.util.Log.d("IngredientRecognizer", "Detected: $text (confidence: ${confidence * 100}%)")
                    
                    if (confidence > 0.55f && isFoodRelated(text)) {
                        val suggestions = getSuggestionsForCategory(text)
                        
                        if (suggestions.isNotEmpty()) {
                            val categoryKey = getCategoryKey(text)
                            if (!processedCategories.contains(categoryKey)) {
                                categories.add(
                                    IngredientCategory(
                                        categoryName = label.text,
                                        suggestions = suggestions
                                    )
                                )
                                processedCategories.add(categoryKey)
                            }
                        }
                    }
                }
                
                android.util.Log.d("IngredientRecognizer", "Categories found: ${categories.size}")
                callback(categories)
            }
            .addOnFailureListener { e ->
                android.util.Log.e("IngredientRecognizer", "Recognition failed", e)
                callback(emptyList())
            }
    }
    
    private fun isFoodRelated(text: String): Boolean {
        return foodKeywords.any { keyword -> text.contains(keyword) || keyword.contains(text) }
    }
    
    private fun getCategoryKey(text: String): String {
        return when {
            text.contains("vegetable") || text.contains("produce") -> "vegetable"
            text.contains("fruit") -> "fruit"
            text.contains("meat") || text.contains("chicken") || text.contains("beef") -> "meat"
            text.contains("seafood") || text.contains("fish") -> "seafood"
            text.contains("dairy") || text.contains("milk") || text.contains("cheese") -> "dairy"
            text.contains("grain") || text.contains("bread") || text.contains("rice") -> "grain"
            else -> text
        }
    }
    
    private fun getSuggestionsForCategory(text: String): List<String> {
        return when {
            text.contains("vegetable") || text.contains("produce") -> listOf(
                "Tomato", "Onion", "Carrot", "Lettuce", "Cabbage",
                "Cucumber", "Bell Pepper", "Broccoli", "Spinach", "Potato",
                "Celery", "Garlic", "Ginger", "Mushroom", "Corn", "Eggplant"
            )
            
            text.contains("fruit") -> listOf(
                "Apple", "Banana", "Orange", "Strawberry", "Grape",
                "Watermelon", "Lemon", "Lime", "Mango", "Pineapple",
                "Blueberry", "Peach", "Pear", "Cherry", "Kiwi"
            )
            
            text.contains("meat") || text.contains("chicken") || text.contains("beef") -> listOf(
                "Chicken", "Beef", "Pork", "Lamb", "Turkey",
                "Chicken Breast", "Ground Beef", "Bacon", "Sausage", "Ham"
            )
            
            text.contains("seafood") || text.contains("fish") -> listOf(
                "Salmon", "Tuna", "Shrimp", "Fish", "Cod",
                "Crab", "Lobster", "Squid", "Tilapia"
            )
            
            text.contains("dairy") || text.contains("milk") || text.contains("cheese") -> listOf(
                "Milk", "Cheese", "Butter", "Yogurt", "Cream",
                "Cheddar", "Mozzarella", "Sour Cream", "Cream Cheese"
            )
            
            text.contains("egg") -> listOf("Egg")
            
            text.contains("grain") || text.contains("bread") || text.contains("rice") || text.contains("pasta") -> listOf(
                "Rice", "Pasta", "Bread", "Flour", "Noodles",
                "Quinoa", "Oats", "Spaghetti", "Couscous"
            )
            
            text.contains("food") || text.contains("ingredient") -> listOf(
                "Chicken", "Beef", "Rice", "Pasta", "Fish",
                "Tomato", "Onion", "Potato", "Egg", "Cheese"
            )
            
            else -> emptyList()
        }
    }
}

