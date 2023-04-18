package com.lifestyle.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlinx.coroutines.CoroutineScope
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lifestyle.user.Sex
import com.lifestyle.user.UserData
import com.lifestyle.user.UserData.Companion.convertToJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@Database(entities = [UserTable::class, WeatherTable::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class LifestyleDatabase : RoomDatabase(){
    abstract fun userDao(): UserDao
    abstract fun weatherDao(): WeatherDao

    companion object {
        @Volatile
        private var mInstance: LifestyleDatabase? = null
        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): LifestyleDatabase {
            return mInstance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LifestyleDatabase::class.java, "LifestyleDatabase.db"
                )
                    .addCallback(RoomDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                mInstance = instance
                instance
            }
        }

        private class RoomDatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                mInstance?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDbTask(database.userDao())
                    }
                }
            }
        }

        suspend fun populateDbTask (userDao: UserDao) {
            val user = UserData()
            user.uuid = UUID.fromString("00000000-0000-0000-0000-000000000000")
            user.name = "Bob Ross"
            user.age = 23
            user.height = 72.0f
            user.weight = 145.0f
            user.sex = Sex.MALE
            user.activityLevel?.caloriesPerHour = 210
            user.activityLevel?.workoutsPerWeek = 3
            user.activityLevel?.averageWorkoutLength = 30
            userDao.insert(convertToJson(user))
        }
    }
}