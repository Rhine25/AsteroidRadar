package com.udacity.asteroidradar.network

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://api.nasa.gov/"
private const val API_KEY = "KVCaUh7L5it7XHdSyy3eYbaD8A9liZ6atT3w5hBU"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface NasaApiService {
    @GET("neo/rest/v1/feed?api_key=$API_KEY")
    fun getAsteroids(@Query("start_date") startDate: String, @Query("end_date") endDate: String): Call<String>

    @GET("planetary/apod?api_key=$API_KEY")
    fun getImageOfTheDay(): Call<String>
}

object NasaApi {
    val retrofitService: NasaApiService by lazy {
        retrofit.create(NasaApiService::class.java)
    }
}