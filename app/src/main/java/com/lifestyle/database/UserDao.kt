package com.lifestyle.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userTable: UserTable)

    @Query("DELETE FROM user_table")
    suspend fun clearTable()

    @Query("SELECT * FROM user_table WHERE :id = id limit 1")
    suspend fun getUser(id: String): UserTable

    @Query("SELECT * FROM user_table")
    fun getAllUsers(): Flow<List<UserTable>>
}