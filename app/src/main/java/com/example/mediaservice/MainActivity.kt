package com.example.mediaservice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var vpService: VPService
    private lateinit var serviceIntent: Intent
    private val mServiceConnection: ServiceConnection by lazy {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                vpService = (service as VPService.MBinder).service()
                vpService.setCloseIt {
                    unbindService(mServiceConnection)
                }
            }
            override fun onServiceDisconnected(name: ComponentName?) {
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        serviceIntent = Intent(this, VPService::class.java)
        serviceIntent.putExtra("track", R.raw.bestsong)
        serviceIntent.putExtra("foreground", true)

        findViewById<ImageView>(R.id.startServ).setOnClickListener {
            bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE)
        }

    }
    override fun onStop() {
        unbindService(mServiceConnection)
        super.onStop()
    }
}