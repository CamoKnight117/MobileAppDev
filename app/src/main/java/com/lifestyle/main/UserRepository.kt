package com.lifestyle.main

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.core.os.HandlerCompat
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.concurrent.Executors

class UserRepository(application: Application) {
    // Live data object notified when we've gotten the location
    public val data : MutableLiveData<UserData> = MutableLiveData<UserData>()

    public fun update() {
        //TODO: put update code here
    }

    companion object {

        @Volatile
        private var instance : UserRepository? = null
        private lateinit var mScope: CoroutineScope
        @Synchronized
        public fun getInstance(application: Application) : UserRepository {
            if(instance==null)
                instance = UserRepository(application)
            return instance!!
        }
    }

    inner class FetchLocationTask{
        var executorService = Executors.newSingleThreadExecutor()
        var mainThreadHandler : Handler = HandlerCompat.createAsync(Looper.getMainLooper())

        public fun execute() {
            executorService.execute {
                try {
                    //TODO: Fetch location data here
                    val jsonData : String = ""
                    postToMainThread(jsonData)
                } catch (e : Exception) {
                    e.printStackTrace()
                }
            }
        }

        private fun postToMainThread(jsonData : String) {
            mainThreadHandler.post {
                try {
                    data.value = Json{ignoreUnknownKeys=true}.decodeFromString<UserData>(jsonData)
                } catch(e : IllegalArgumentException) {
                    e.printStackTrace()
                } catch(e : SerializationException) {
                    e.printStackTrace()
                }

            }
        }
    }


}