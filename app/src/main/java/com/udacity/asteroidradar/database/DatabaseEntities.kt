package com.udacity.asteroidradar.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay

@Entity(tableName = "asteroids")
data class DatabaseAsteroid constructor(
    @PrimaryKey
    val id: Long,
    val codename: String,
    val closeApproachDate: String,
    val absoluteMagnitude: Double,
    val estimatedDiameter: Double,
    val relativeVelocity: Double,
    val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
)

fun List<DatabaseAsteroid>.asAsteroidModel() :List<Asteroid>{
    return map {
        Asteroid(
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }
}

@Entity(tableName = "pictureOfDay")
data class DatabasePictureOfDay(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val date: String,
    val mediaType: String,
    val title: String,
    val url: String
)

fun DatabasePictureOfDay.asPictureModel() : PictureOfDay {
    return PictureOfDay(
        date = this.date,
        mediaType = this.mediaType,
        title = this.title,
        url = this.url
    )
}