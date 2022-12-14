package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.api.getPreviousDay
import com.udacity.asteroidradar.database.AppDatabase
import com.udacity.asteroidradar.repository.Repository
import retrofit2.HttpException

class RefreshDataWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        val database = AppDatabase.getInstance(applicationContext)
        val repository = Repository(database)

        return try {
            repository.refreshAsteroids()
            repository.refreshPicture()
            repository.deletePreviousDayAsteroid(getPreviousDay())
            Result.success()
        }catch (e : HttpException){
            Result.retry()
        }
    }
}