package com.example.systemserviceclientapp.random_number_service

import android.content.ComponentName
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log

object RandomNumberServiceContract {

    const val ACTION_GET_RANDOM_NUMBER = 1

    fun componentName() : ComponentName {
        return ComponentName(
            "com.example.systemserviceserverapp",
            "com.example.systemserviceserverapp.services.RandomNumberService"
        )
    }

}

class ReceiveRandomNumberHandler constructor(val onRandomNumberReceived : (String) -> Unit)
    : Handler(Looper.getMainLooper()) {

    override fun handleMessage(msg: Message) {
        Log.d("MainActivity", "ReceiveRandomNumberHandler handleMessage Called, = what ${msg.what}")

        when(msg.what) {
            RandomNumberServiceContract.ACTION_GET_RANDOM_NUMBER -> {

                val randomNumberResult = msg.data.getString("randomNumberResult")

                if(randomNumberResult != null)
                    onRandomNumberReceived(randomNumberResult)
            }
        }

        super.handleMessage(msg)
    }

}