package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AsteroidDao {
    @Query("SELECT * FROM asteroids ORDER BY closeApproachDate ASC")
    suspend fun getAllAsteroids(): List<DatabaseAsteroid>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAsteroids(vararg asteroid: DatabaseAsteroid)

    @Query("select * from asteroids where closeApproachDate between :startDate and :endDate order by closeApproachDate asc")
    suspend fun getStretchAsteroids(startDate: String, endDate: String): List<DatabaseAsteroid>

    @Query("DELETE FROM asteroids WHERE closeApproachDate = :previousDay")
    fun deleteOldAsteroids(previousDay: String)
}

@Dao
interface PictureDao {
    @Query("SELECT * FROM pictureOfDay order by date desc limit 1")
    fun getPictureOfDay(): LiveData<DatabasePictureOfDay?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPictureOfDay(pictureOfDay: DatabasePictureOfDay)
}

@Database(entities = [DatabaseAsteroid::class, DatabasePictureOfDay::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract val asteroidDao: AsteroidDao
    abstract val pictureDao: PictureDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "asteroid"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}