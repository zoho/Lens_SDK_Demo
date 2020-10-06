package com.zoho.lens

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import com.zoho.assistrtc.socket.Constants


class NetworkListenerService : BroadcastReceiver() {
    lateinit var c: Context
    var wifiConnected: Boolean? = false


    override fun onReceive(context: Context, intent: Intent) {
        c = context
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var activeNetInfo: NetworkInfo? = null
        activeNetInfo = connectivityManager.activeNetworkInfo
        if (activeNetInfo != null) {
            if (activeNetInfo.isConnected) {
                if (LensApplication.getCurrentActivity(context) != null && !LensApplication.getCurrentActivity(context)?.className?.contains(
                        "AssistSessioxn"
                    )!! && activeNetInfo.type == ConnectivityManager.TYPE_WIFI
                ) {
                    if ((!wifiConnected!!)) {
                        wifiConnected = true
                        var networkIntent = Intent(Constants.NETWORK_CHANGE_FILTER)
                        networkIntent.putExtra(Constants.NETWORK_CHANGE_MESSAGE, false)
                        Log.d("network state", "forced disconnected")//no i18n
                        context.sendBroadcast(networkIntent)
                        try {
                            Thread.sleep(1000)
                        } catch (e: InterruptedException) {
                            Log.d("network state", "sleep thread failed")//no i18n
                        }

                        networkIntent = Intent(Constants.NETWORK_CHANGE_FILTER)
                        networkIntent.putExtra(Constants.NETWORK_CHANGE_MESSAGE, true)
                        Log.d("network state", "connected")//no i18n
                        context.sendBroadcast(networkIntent)
                    } else {
                        wifiConnected = false
                    }
                } else {
                    val networkIntent = Intent(Constants.NETWORK_CHANGE_FILTER)
                    networkIntent.putExtra(Constants.NETWORK_CHANGE_MESSAGE, true)
                    Log.d("network state", "connected")//no i18n
                    context.sendBroadcast(networkIntent)
                }
            }
        } else {
            wifiConnected = false
            val networkIntent = Intent(Constants.NETWORK_CHANGE_FILTER)
            networkIntent.putExtra(Constants.NETWORK_CHANGE_MESSAGE, false)
            Log.d("network state", "disconnected")//no i18n
            context.sendBroadcast(networkIntent)
        }
    }
}
