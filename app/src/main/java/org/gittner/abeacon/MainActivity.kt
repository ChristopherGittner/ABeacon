package org.gittner.abeacon

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import org.gittner.abeacon.ui.theme.ABeaconTheme
import java.util.UUID


class MainActivity : ComponentActivity() {

    private var bluetoothPermissionOk : MutableState<Boolean> = mutableStateOf(false)
    private var locationPermissionOk : MutableState<Boolean> = mutableStateOf(false)
    private var notificationPermissionOk : MutableState<Boolean> = mutableStateOf(false)
    private var batteryOptimizationDisabled : MutableState<Boolean> = mutableStateOf(false)
    private var uuid : MutableState<String> = mutableStateOf("")

    object Preferences {
        const val PREFS_NAME = "prefs"
        const val UUID = "uuid"
    }

    private val requestPermissionsLauncherStage1 = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        bluetoothPermissionOk.value = (permissions[android.Manifest.permission.BLUETOOTH_ADVERTISE] == true
                && permissions[android.Manifest.permission.BLUETOOTH_SCAN] == true
                && permissions[android.Manifest.permission.BLUETOOTH_CONNECT] == true)
        notificationPermissionOk.value = (permissions[android.Manifest.permission.POST_NOTIFICATIONS] == true)

        if (bluetoothPermissionOk.value && notificationPermissionOk.value) {
            Log.i("MainActivity Permissions Stage 1", "All permissions granted")
            // Start the BackgroundService
            startService(Intent(this, BackgroundService::class.java))
        } else {
            Log.e("MainActivity Permissions Stage 1", "Not all permissions granted: $permissions")
        }

        requestPermissionsLauncherStage2.launch(arrayOf(
            android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ))
    }

    private val requestPermissionsLauncherStage2 = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        locationPermissionOk.value = (permissions[android.Manifest.permission.ACCESS_BACKGROUND_LOCATION] == true)

        if (locationPermissionOk.value) {
            Log.i("MainActivity Permissions Stage 2", "Location permission granted")
            // Start the BackgroundService
            startService(Intent(this, BackgroundService::class.java))

            requestBackgroundOptimizationResult.launch(null)
        } else {
            Log.e("MainActivity Permissions Stage 2", "Location permission not granted: $permissions")
        }
    }

    private val requestBackgroundOptimizationResult = registerForActivityResult(DisableBgOptimization()) {
        // Check if Battery optimization is disabled
        val pm = applicationContext.getSystemService(POWER_SERVICE) as android.os.PowerManager
        batteryOptimizationDisabled.value = pm.isIgnoringBatteryOptimizations(packageName)

        if (!batteryOptimizationDisabled.value) {
            Log.e("MainActivity", "Battery optimization not disabled")
        } else {
            Log.i("MainActivity", "Battery optimization disabled")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences(Preferences.PREFS_NAME, MODE_PRIVATE)

        // Check if we have a uuid already stored. If not, generate one and store it
        uuid.value = prefs.getString(Preferences.UUID, "") ?: ""
        if (uuid.value == "") {
            Log.i("MainActivity", "No UUID found, generating one")
            uuid.value = UUID.randomUUID().toString()
            getSharedPreferences("prefs", MODE_PRIVATE).edit { putString(Preferences.UUID, uuid.value) }
        }

        // Set the content view
        enableEdgeToEdge()
        setContent {
            ABeaconTheme {
                Box (Modifier
                    .windowInsetsPadding(WindowInsets.statusBars)
                ) {
                    Box(Modifier
                        .windowInsetsPadding(WindowInsets.safeDrawing)
                        .fillMaxSize()
                        .padding(16.dp)
                    ) {
                        MainScreen(
                            uuid.value,
                            bluetoothPermissionOk.value,
                            locationPermissionOk.value,
                            notificationPermissionOk.value,
                            batteryOptimizationDisabled.value)
                    }
                }
            }
        }

        // Request permissions
        Log.i("MainActivity", "Requesting permissions")
        requestPermissionsLauncherStage1.launch(arrayOf(
            android.Manifest.permission.BLUETOOTH_ADVERTISE,
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.POST_NOTIFICATIONS
        ))
    }

    fun copyUUIDToClipboard() : () -> Unit = {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("UUID", uuid.value)
        clipboard.setPrimaryClip(clip)

        Log.i("MainActivity", "UUID copied to clipboard")

        Toast.makeText(this, "UUID copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    @Composable
    fun MainScreen(
        uuid: String,
        bluetoothPermissionOk: Boolean,
        locationPermissionOk: Boolean,
        notificationPermissionOk: Boolean,
        batteryOptimizationOk: Boolean) {
        Column {
            Text("UUID: $uuid", modifier = Modifier.clickable(enabled = true,onClick = copyUUIDToClipboard()))
            Text("(Tap UUID to copy to clipboard)", fontSize = 12.dp.value.sp, color = Color.Gray)
            Text(
                text = if (bluetoothPermissionOk) "Bluetooth Permission granted" else "Bluetooth Permission not granted",
                color = if (bluetoothPermissionOk) Color.Green else Color.Red,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = if (locationPermissionOk) "Location Permission granted" else "Location Permission not granted",
                color = if (locationPermissionOk) Color.Green else Color.Red
            )
            Text(
                text = if (notificationPermissionOk) "Notification Permission granted" else "Notification Permission not granted",
                color = if (notificationPermissionOk) Color.Green else Color.Red
            )
            Text(
                text = if (batteryOptimizationOk) "Battery optimization disabled" else "Battery optimization enabled",
                color = if (batteryOptimizationOk) Color.Green else Color.Red
            )
        }
    }
}

