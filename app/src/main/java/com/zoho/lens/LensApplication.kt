package com.zoho.lens

import android.app.ActivityManager
import android.app.Application
import android.content.ComponentName
import android.content.Context
import com.squareup.leakcanary.LeakCanary

class LensApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
    }

    companion object {
        fun getCurrentActivity(context: Context): ComponentName? {
            try {

                // Using ACTIVITY_SERVICE with getSystemService(String)
                // to retrieve a ActivityManager for interacting with the global system state.

                val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

                // Return a list of the tasks that are currently running,
                // with the most recent being first and older ones after in order.
                // Taken 1 inside getRunningTasks method means want to take only
                // top activity from stack and forgot the olders.

                val alltasks = am.getRunningTasks(1)
                return alltasks[0].topActivity
            } catch (t: Throwable) {
            }
            return null
        }
    }
}