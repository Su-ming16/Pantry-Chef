package com.example.pantrychef.data.network.dto

import com.google.gson.annotations.SerializedName

data class MealsResponseDto(
    @SerializedName("meals") val meals: List<MealDto>?
)