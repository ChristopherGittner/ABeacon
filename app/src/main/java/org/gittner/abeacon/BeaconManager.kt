package org.gittner.abeacon

import android.app.Service.MODE_PRIVATE
import android.content.Context
import android.util.Log
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.BeaconTransmitter
import org.gittner.abeacon.MainActivity.Preferences

class BeaconManager {
    companion object {
        private lateinit var beacon: Beacon
        private val beaconParser: BeaconParser = BeaconParser()
            .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")

        private lateinit var beaconTransmitter : BeaconTransmitter

        fun startBeacon(context: Context) {
            // Check if we have a uuid already stored. If not, generate one and store it
            val uuid = context.getSharedPreferences("prefs", MODE_PRIVATE)
                .getString(Preferences.UUID, null)
                ?: throw RuntimeException("UUID not found in shared preferences")

            // Check if vars are initialized and do so if not
            if (!this::beacon.isInitialized) {
                beacon = Beacon.Builder()
                    .setId1(uuid)
                    .setId2("1")
                    .setId3("2")
                    .setManufacturer(0x004c)
                    .setTxPower(-59)
                    .build()

                beaconTransmitter = BeaconTransmitter(context, beaconParser)
            }

            // Start the Beacon
            Log.i("BeaconManager", "Starting Beacon with UUID: $uuid")
            beaconTransmitter.startAdvertising(beacon)
        }
    }
}