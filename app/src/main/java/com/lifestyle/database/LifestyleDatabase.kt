package com.lifestyle.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lifestyle.bmr.ActivityLevel
import com.lifestyle.main.LifestyleApplication
import com.lifestyle.user.LastUsedModule
import com.lifestyle.user.Sex
import com.lifestyle.user.UserData
import com.lifestyle.user.UserData.Companion.convertToJson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@Database(entities = [UserTable::class, WeatherTable::class], version = 3, exportSchema = false)
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
                    .addCallback(RoomDatabaseCallback(scope, context as LifestyleApplication))
                    .fallbackToDestructiveMigration()
                    .build()
                mInstance = instance
                instance
            }
        }

        private class RoomDatabaseCallback(
            private val scope: CoroutineScope,
            private val application: LifestyleApplication
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                mInstance?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDbTask(database.userDao(), application)
                    }
                }
            }
        }

        suspend fun populateDbTask (userDao: UserDao, application: LifestyleApplication) {
            val user = UserData()
            user.id = "0"
            user.name = "Bob Ross"
            user.age = 23
            user.height = 72.0f
            user.weight = 145.0f
            user.sex = Sex.MALE
            user.activityLevel = ActivityLevel()
            user.activityLevel?.caloriesPerHour = 210
            user.activityLevel?.workoutsPerWeek = 3
            user.activityLevel?.averageWorkoutLength = 30
            user.lastUsedModule = LastUsedModule.MAIN
            val table = convertToJson(user)
            userDao.insert(table)
            application.userRepository.fetchUserData(user.id!!)
        }
    }
}