package com.example.pantrychef.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.pantrychef.data.network.TheMealDbApiService

object ApiClient {
    private const val BASE_URL = "https://www.themealdb.com/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: TheMealDbApiService by lazy {
        retrofit.create(TheMealDbApiService::class.java)
    }
}