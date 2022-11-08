package com.udacity.asteroidradar.repository

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.asDatabaseModel
import com.udacity.asteroidradar.api.getDate
import com.udacity.asteroidradar.api.getToday
import com.udacity.asteroidradar.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository constructor(private val database: AppDatabase) {

    val pictureOfDay = Transformations.map(database.pictureDao.getPictureOfDay()) {
        it?.asPictureModel()
    }

    private var _asteroids = MutableLiveData<List<DatabaseAsteroid>>()
    val asteroids
        get() = Transformations.map(_asteroids) {
            it.asAsteroidModel()
        }

    suspend fun refreshAsteroids(){
        withContext(Dispatchers.IO){
            val startDate = getToday()
            val endDate = getDate(7)
            val asteroids = AsteroidApi.retrofitService.getAsteroidsAsync(Constants.API_KEY, startDate, endDate).await()
            database.asteroidDao.insertAllAsteroids(*asteroids.asDatabaseModel())
        }
    }

    suspend fun refreshPicture(){
        withContext(Dispatchers.IO){
            val picture= AsteroidApi.retrofitService.getDailyImgAsync(Constants.API_KEY)
            database.pictureDao.insertPictureOfDay(
                DatabasePictureOfDay(
                    date = picture.date,
                    url = picture.url,
                    mediaType = picture.mediaType,
                    title = picture.title
                )
            )
        }
    }

    suspend fun getAllAsteroids(){
        _asteroids.value = database.asteroidDao.getAllAsteroids()
    }

    suspend fun getWeekAsteroids(){
        val startDate = getToday()
        val endDate = getDate(6)
        _asteroids.value = database.asteroidDao.getStretchAsteroids(startDate, endDate)
    }

    suspend fun getTodayAsteroids() {
        _asteroids.value = database.asteroidDao.getStretchAsteroids(getToday(), getToday())
    }

    fun deletePreviousDayAsteroid(previousDayString: String){
        database.asteroidDao.deleteOldAsteroids(previousDayString)
    }
}