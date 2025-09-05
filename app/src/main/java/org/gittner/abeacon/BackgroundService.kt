package org.gittner.abeacon

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log

class BackgroundService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("BackgroundService", "Registering Bluetooth receiver")
        registerReceiver(BluetoothBroadcastReceiver(), IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))

        Log.i("BackgroundService", "Starting Service")

        // Create a notification channel (for Android 8.0 and above)
        createNotificationChannel()

        // Create a notification for the foreground service
        val notification = createNotification()

        // Start the service in the foreground
        startForeground(1, notification)

        BeaconManager.startBeacon(applicationContext)

        return START_STICKY
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "my_channel_id",
            "Foreground Service",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        return Notification.Builder(this, "my_channel_id")
            .setContentTitle("Foreground Service")
            .setContentText("Running in Foreground")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}