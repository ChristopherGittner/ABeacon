package org.gittner.abeacon

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BluetoothBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context !== null && intent?.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
            if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR) == BluetoothAdapter.STATE_ON) {
                Log.i("Tag", "Bluetooth enabled")

                BeaconManager.startBeacon(context)
            }
        }
    }
}