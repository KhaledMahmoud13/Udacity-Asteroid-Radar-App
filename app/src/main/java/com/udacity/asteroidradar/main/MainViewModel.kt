package com.udacity.asteroidradar.main

import androidx.lifecycle.*
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.repository.Repository
import kotlinx.coroutines.launch

enum class Status { LOADING, ERROR, DONE }

enum class Filter { SAVED, WEEK, TODAY }

class MainViewModel(private val repository: Repository) : ViewModel() {
    private val _status = MutableLiveData<Status>()
    val status: LiveData<Status>
        get() = _status

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    var filter = Filter.SAVED
        set(value) {
            field = value
            displayAsteroids()
        }

    val nasaDailyImg = repository.pictureOfDay
    val nasaAsteroids = repository.asteroids

    init {
        getAsteroids()
        retrieveDailyImg()
        displayAsteroids()
    }


    private fun retrieveDailyImg() {
        viewModelScope.launch {
            try {
                repository.refreshPicture()
                val pictureOfDay = AsteroidApi.retrofitService.getDailyImgAsync(Constants.API_KEY)
                _pictureOfDay.postValue(pictureOfDay)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getAsteroids() {
        _status.value = Status.LOADING
        viewModelScope.launch {
            try {
                repository.refreshAsteroids()
                _status.value = Status.DONE
                displayAsteroids()
            } catch (exception: Exception) {
                exception.printStackTrace()
                _status.value = Status.ERROR
            }
        }
    }

    private fun displayAsteroids() {
        viewModelScope.launch {
            try {
                when (filter) {
                    Filter.SAVED -> repository.getAllAsteroids()
                    Filter.WEEK -> repository.getWeekAsteroids()
                    Filter.TODAY -> repository.getTodayAsteroids()
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }

    class MainViewModelFactory(private val appRepository: Repository) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                MainViewModel(appRepository) as T
            } else {
                throw IllegalArgumentException("Main ViewModel Not Found")
            }
        }

    }
}
