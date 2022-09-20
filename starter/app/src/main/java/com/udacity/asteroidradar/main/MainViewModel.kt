package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.asteroidradar.network.NasaApi
import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _imageResponse = MutableLiveData<String>()
    val imageResponse: LiveData<String>
        get() = _imageResponse

    private val database = getDatabase(application)
    private val asteroidsRepository = AsteroidsRepository(database)

    val asteroids = database.asteroidDao.getAsteroids()

    init {
        getImageOfTheDay()
        viewModelScope.launch {
            asteroidsRepository.refreshAsteroids()
        }
    }

    private fun getImageOfTheDay() {
        NasaApi.retrofitService.getImageOfTheDayAsync().enqueue(object: Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                _imageResponse.value = "Failure: ${t.message}"
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                _imageResponse.value = response.body()
            }

        })
    }
}