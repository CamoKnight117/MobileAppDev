package com.example.lifestyle

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.lifestyle.database.LifestyleDatabase
import com.lifestyle.database.UserDao
import com.lifestyle.database.UserTable
import java.io.IOException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach

/**
 * Class for testing user DAO. Tests are inspired by tests from https://developer.android.com/training/data-storage/room/testing-db
 */
class UserDaoTests {
    private lateinit var userDao: UserDao
    private lateinit var db: LifestyleDatabase

    @BeforeEach
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, LifestyleDatabase::class.java).build()
        userDao = db.userDao()
    }

    @AfterEach
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    suspend fun writeAndReadUser() {
        val user = UserTable("1", "json", "loc", ByteArray(1))
        userDao.insert(user)
        val userFromDatabase = userDao.getUser("1")
        assertEquals(userFromDatabase.id, user.id)
        assertEquals(userFromDatabase.userJson, user.userJson)
        assertEquals(userFromDatabase.textLocationJson, user.textLocationJson)
        assertEquals(userFromDatabase.profilePic, user.profilePic)
    }
}