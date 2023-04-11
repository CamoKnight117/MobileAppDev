package com.lifestyle

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.PersistableBundle
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnitRunner
import java.io.File

class LifestyleTestRunner : AndroidJUnitRunner() {
    //val targetContext = InstrumentationRegistry.getInstrumentation().targetContext

//    override fun callActivityOnCreate(
//        activity: Activity?,
//        icicle: Bundle?,
//        persistentState: PersistableBundle?
//    ) {
//        deleteLocalStorage()
//        super.callActivityOnCreate(activity, icicle, persistentState)
//    }

//    override fun callApplicationOnCreate(app: Application?) {
//        deleteLocalStorage()
//        super.callApplicationOnCreate(app)
//    }

    private fun deleteLocalStorage() {
        val files: Array<File> = targetContext.getFilesDir().listFiles()
        if (files != null) {
            for (file in files) {
                file.delete()
            }
        }
    }
}