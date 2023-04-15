package com.lifestyle.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userTable: UserTable)

    @Query("DELETE FROM user_table")
    suspend fun clearTable()

    @Query("SELECT * FROM user_table")
    fun getAllWeather(): Flow<List<UserTable>>
}