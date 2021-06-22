package com.example.weatherlens.network

import com.example.weatherlens.activity.mainActivity.model.ForecastData
import com.example.weatherlens.activity.mainActivity.model.ForecastResponse
import com.example.weatherlens.activity.mainActivity.model.WeatherResponse
import com.example.weatherlens.utils.Constants.Companion.APP_ID
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherAPI {
    @GET("weather")
    suspend fun getWeather(
        @Query("q")
        city: String,
        @Query("APPID")
        appId: String = APP_ID
    ): Response<WeatherResponse>

    @GET("forecast")
    suspend fun getForecast(
        @Query("q")
        city: String,
        @Query("APPID")
        appId: String = APP_ID
    ): Response<ForecastResponse>
}