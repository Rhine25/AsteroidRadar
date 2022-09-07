package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.asteroidradar.network.NasaApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {

    private val _imageResponse = MutableLiveData<String>()
    val imageResponse: LiveData<String>
        get() = _imageResponse

    private val _asteroidsResponse = MutableLiveData<String>()
    val asteroidsResponse: LiveData<String>
        get() = _asteroidsResponse

    init {
        getImageOfTheDay()
        getAsteroids()
    }

    private fun getImageOfTheDay() {
        NasaApi.retrofitService.getImageOfTheDay().enqueue(object: Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                _imageResponse.value = "Failure: ${t.message}"
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                _imageResponse.value = response.body()
            }

        })
    }

     private fun getAsteroids() {
         NasaApi.retrofitService.getAsteroids("2022-09-05", "2022-09-12").enqueue(object: Callback<String> {
             override fun onFailure(call: Call<String>, t: Throwable) {
                 _asteroidsResponse.value = "Failure: ${t.message}"
             }

             override fun onResponse(call: Call<String>, response: Response<String>) {
                 _asteroidsResponse.value = response.body()
             }

         })
     }
}