package com.example.mediaservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MBR : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        VPService.todo?.let{ it() }
    }
}
class CloseReciever : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        VPService.closeIt?.let { it() }
    }
}
