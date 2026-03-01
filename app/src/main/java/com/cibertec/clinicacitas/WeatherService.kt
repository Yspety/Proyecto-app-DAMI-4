package com.cibertec.clinicacitas

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// Modelo de datos: Lo que recibimos de la API
data class WeatherResponse(
    val main: Main,
    val weather: List<WeatherDescription>,
    val name: String
)
data class Main(val temp: Double)
data class WeatherDescription(val description: String)

// Interfaz de la API
interface WeatherService {
    @GET("weather")
    fun getCurrentWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String // Añade este si no lo tenías
    ): Call<WeatherResponse>
}