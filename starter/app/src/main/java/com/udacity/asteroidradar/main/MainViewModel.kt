package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.asteroidradar.network.NasaApi
import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.api.getSeventhDayFormatted
import com.udacity.asteroidradar.api.getTodayFormatted
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.detail.PictureOfDay
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

enum class NasaApiStatus { LOADING, ERROR, DONE }
enum class NasaApiFilter { ALL, WEEK, TODAY }

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _status = MutableLiveData<NasaApiStatus>()

    private val _imageOfTheDay = MutableLiveData<PictureOfDay>()
    val imageOfTheDay: LiveData<PictureOfDay>
        get() = _imageOfTheDay

    private val _navigateToAsteroid = MutableLiveData<Asteroid>()
    val navigateToAsteroid: LiveData<Asteroid>
    get() = _navigateToAsteroid

    private val database = getDatabase(application)
    private val asteroidsRepository = AsteroidsRepository(database)

    private val asteroidsFilter = MutableLiveData<NasaApiFilter>(NasaApiFilter.TODAY)

    var asteroids =
        Transformations.switchMap(asteroidsFilter) { filter ->
            when (filter) {
                NasaApiFilter.WEEK -> asteroidsRepository.weekAsteroids
                NasaApiFilter.TODAY -> asteroidsRepository.todayAsteroids
                else -> asteroidsRepository.asteroids
            }
        }

    init {
        getImageOfTheDay()
        viewModelScope.launch {
            asteroidsRepository.refreshAsteroids()
        }
    }

    private fun getImageOfTheDay() {
        viewModelScope.launch {
            _status.value = NasaApiStatus.LOADING
            try {
                NasaApi.retrofitService.getImageOfTheDayAsync().enqueue(object: Callback<PictureOfDay> {
                    override fun onFailure(call: Call<PictureOfDay>, t: Throwable) {
                        _status.value = NasaApiStatus.ERROR
                    }
                    override fun onResponse(call: Call<PictureOfDay>, response: Response<PictureOfDay>) {
                        _imageOfTheDay.value = response.body()
                        _status.value = NasaApiStatus.DONE
                    }
                })
            } catch (e: Exception) {
                _status.value = NasaApiStatus.ERROR
            }
        }
    }

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToAsteroid.value = asteroid
    }

    fun displayAsteroidDetailsComplete() {
        _navigateToAsteroid.value = null
    }

    fun updateFilter(filter: NasaApiFilter) {
        asteroidsFilter.postValue(filter)
    }
}