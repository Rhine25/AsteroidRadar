package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import retrofit2.HttpException
import java.lang.Exception

class FetchAsteroidsWorker(appContext: Context, params: WorkerParameters): CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "FetchAsteroidsWork"
    }

    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = AsteroidsRepository(database)

        return try {
            repository.refreshAsteroids()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        } catch (e: Exception) {
            Result.failure()
        }

    }

}