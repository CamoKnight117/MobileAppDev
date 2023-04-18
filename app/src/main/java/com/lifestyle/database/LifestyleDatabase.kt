package com.lifestyle.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlinx.coroutines.CoroutineScope
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lifestyle.user.Sex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@Database(entities = [UserTable::class, WeatherTable::class], version = 1, exportSchema = false)
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
            userDao.insert(UserTable(UUID.randomUUID(), "Bob Ross", 23, 72.0f, 145.0f, Sex.MALE))
        }
    }
}