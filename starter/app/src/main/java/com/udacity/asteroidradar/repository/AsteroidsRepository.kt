package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.api.getSeventhDayFormatted
import com.udacity.asteroidradar.api.getTodayFormatted
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.network.NasaApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.Exception

class AsteroidsRepository(private val database: AsteroidsDatabase) {
    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroids(getTodayFormatted())) {
        it.asDomainModel()
    }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val response = NasaApi.retrofitService.getAsteroidsAsync(getTodayFormatted(), getSeventhDayFormatted()).await()
                val asteroids = parseAsteroidsJsonResult(JSONObject(response))
                val databaseAsteroids = asteroids.map {
                    it.asDatabaseModel()
                }.toTypedArray()
                database.asteroidDao.insertAll(*databaseAsteroids)
            } catch (e: Exception) {
            }
        }
    }
}