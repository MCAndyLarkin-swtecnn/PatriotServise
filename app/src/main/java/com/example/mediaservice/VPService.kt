package com.example.mediaservice

import android.app.*
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.*
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*



class VPService : Service() {
    companion object {
        var todo: (() -> Unit)? = null
        var closeIt: (() -> Unit)? = null
    }
    inner class MBinder: Binder(){
        fun service(): VPService{
            return this@VPService
        }
    }

    private val notificationManager: NotificationManager by lazy {
        getSystemService(NotificationManager::class.java)
    }
    private lateinit var builder: NotificationCompat.Builder
    private val VPID = "Putin"
    private val foregroundID = VPID.hashCode()

    private val song: MediaPlayer by lazy {
        val mp = MediaPlayer.create(this, track)
        mp.setOnCompletionListener {
            it.start()
//        controlBut.backgroundTintList =
//            resources.getColorStateList(R.color.design_default_color_error)
        }
        mp
    }
    private var track: Int = R.raw.bestsong

    private lateinit var progressJob: Job
    private val scope: CoroutineScope by lazy { CoroutineScope(Job()) }

    fun setCloseIt(p:() -> Unit){
        closeIt = p
    }

    override fun onBind(intent: Intent): IBinder {
        track = intent.getIntExtra("track", R.raw.bestsong)

        if (intent.getBooleanExtra("foreground", true)) {
            todo = {
                changeState()
            }
            val remoteView = RemoteViews(packageName, R.layout.notify)

            remoteView.setOnClickPendingIntent(
                R.id.playpause_but,
                PendingIntent.getBroadcast(
                    this, 100,
                    Intent(
                        this,
                        MBR::class.java
                    ), 0)
            )
            remoteView.setOnClickPendingIntent(
                R.id.resotka,
                PendingIntent.getBroadcast(
                    this, 101,
                    Intent(this, CloseReciever::class.java), 0
                )
            )

            notificationManager.createNotificationChannel(
                NotificationChannel(
                    VPID,
                    "Vladimir Putin",
                    NotificationManager.IMPORTANCE_HIGH
                )
            )

            builder = NotificationCompat.Builder(this, VPID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("VPService")
                .setContentText("Vladimir Putin")
                .setColor(Color.BLUE)
                .setCustomContentView(remoteView)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setNotificationSilent()
                .setOnlyAlertOnce(true)

            startForeground(foregroundID, builder.build())
        } else {
            changeState()
        }
        return MBinder()
    }
    private fun launchProgress(){
        progressJob = scope.launch {
            while (true) {
                if (!isActive) break
                delay(250L)
                notificationManager.notify(
                    foregroundID,
                    builder
//                        .setNotificationSilent()
                        .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                        .setProgress(
                            100,
                            song.currentPosition * 100 / song.duration,
                            false
                        )
                        .build()
                )
            }
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        scope.cancel()
        song.stop()
        notificationManager.cancelAll()
        return super.onUnbind(intent)
    }

    private fun changeState(){
        if (song.isPlaying){
            progressJob.cancel()
            song.pause()
//        controlBut.backgroundTintList =
//            resources.getColorStateList(R.color.design_default_color_error)
        }else{
            launchProgress()
            song.start()
//        remoteView.f.backgroundTintList = resources.getColorStateList(R.color.grn)
        }
    }
    override fun onDestroy() {
        todo = null
        closeIt = null
        super.onDestroy()
    }
}