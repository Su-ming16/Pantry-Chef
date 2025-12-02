package com.example.pantrychef.data.model

enum class DietaryPreference(
    val displayName: String,
    val description: String,
    val icon: String
) {
    NONE(
        displayName = "No Preference",
        description = "Show all recipes without filtering",
        icon = "🍽️"
    ),
    FITNESS(
        displayName = "Fitness",
        description = "High protein, grilled/baked, low fat",
        icon = "💪"
    ),
    WEIGHT_LOSS(
        displayName = "Weight Loss",
        description = "Low calorie, light meals, lots of vegetables",
        icon = "🥗"
    ),
    VEGETARIAN(
        displayName = "Vegetarian",
        description = "No meat or seafood",
        icon = "🌱"
    ),
    QUICK_EASY(
        displayName = "Quick & Easy",
        description = "Simple ingredients, fast preparation",
        icon = "⚡"
    ),
    KID_FRIENDLY(
        displayName = "Kid Friendly",
        description = "Mild flavors, familiar ingredients",
        icon = "👶"
    );

    companion object {
        fun fromString(value: String): DietaryPreference {
            return entries.find { it.name == value } ?: NONE
        }
    }
}

