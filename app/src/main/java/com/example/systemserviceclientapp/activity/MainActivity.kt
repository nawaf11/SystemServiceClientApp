package com.example.systemserviceclientapp.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.systemserviceclientapp.random_number_service.RandomNumberServiceContract
import com.example.systemserviceclientapp.random_number_service.ReceiveRandomNumberHandler
import com.example.systemserviceclientapp.ui.theme.SystemServiceClientAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var serviceRequestMessenger: Messenger? = null
    private var serviceResponseMessenger: Messenger? = null
    lateinit var serviceIntent : Intent

    private val randomServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.d("MainActivity", "onServiceConnected")

            serviceRequestMessenger = Messenger(service)
            serviceResponseMessenger = Messenger(ReceiveRandomNumberHandler(
                onRandomNumberReceived = {
                    viewModel.onRandomNumberChanged(it)
                }
            ))

            isRandomNumberServiceBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.d("MainActivity", "onServiceDisconnected")

            serviceRequestMessenger = null
            serviceResponseMessenger = null
            isRandomNumberServiceBound = false
        }
    }

    /** To Track binding state of the service  */
    private var isRandomNumberServiceBound : Boolean = false

    private val viewModel by viewModels<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        serviceIntent = Intent().apply {
            component = RandomNumberServiceContract.componentName()
            setPackage(packageName)
        }

        setContent {
            SystemServiceClientAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    Column(Modifier.padding(top = 32.dp, bottom = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally) {

                        Button(onClick = {
                            getNewRandomNumberFromRandomNumberService()
                        }) {
                            Text(text = "Get New Number")
                        }

                        val currentText by viewModel.currentTextState

                        Greeting(currentText)
                    }
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()

        bindRandomNumberService()
    }

    override fun onStop() {
        super.onStop()
        unbindService(randomServiceConnection)
    }

    private fun bindRandomNumberService() {

        try {
            val res = this.bindService(serviceIntent, randomServiceConnection,
                Context.BIND_AUTO_CREATE)

            Log.d("MainActivity", "bindService res = $res")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getNewRandomNumberFromRandomNumberService() {
        Log.d("MainActivity", "getNewRandomNumberFromRandomNumberService called, isRandomNumberServiceBound = $isRandomNumberServiceBound")

        if (! isRandomNumberServiceBound)
            return

        // Create and send a message to the service, using a supported 'what' value.
        val msg: Message = Message.obtain(null, RandomNumberServiceContract.ACTION_GET_RANDOM_NUMBER)
        msg.replyTo = serviceResponseMessenger

        try {
            serviceRequestMessenger?.send(msg)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }


}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = name,
        modifier = modifier
    )
}
