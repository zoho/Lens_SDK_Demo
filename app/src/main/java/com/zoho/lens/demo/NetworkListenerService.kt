package com.zoho.lens.demo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo


@Suppress("DEPRECATION")
class NetworkListenerService : BroadcastReceiver() {
    lateinit var c: Context
    var wifiConnected: Boolean? = false

    override fun onReceive(context: Context, intent: Intent) {
        c = context
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var activeNetInfo: NetworkInfo? = null
        activeNetInfo = connectivityManager.activeNetworkInfo
    }
}
