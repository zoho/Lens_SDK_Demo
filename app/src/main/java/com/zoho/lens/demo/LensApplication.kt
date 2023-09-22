package com.zoho.lens.demo

import android.app.Application
//import com.squareup.leakcanary.LeakCanary
import com.zoho.lens.LensSDK

class LensApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        LensSDK.setToken(this, "YOUR_TOKEN") //you need to set your token here
        /*if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)*/
    }
}