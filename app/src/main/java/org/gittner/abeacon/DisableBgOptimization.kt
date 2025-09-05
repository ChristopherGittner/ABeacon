package org.gittner.abeacon

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.net.toUri

class DisableBgOptimization : ActivityResultContract<Void?, Void?>()
{
    override fun createIntent(
        context: Context,
        input: Void?
    ): Intent {
        val packageName = context.packageName
        return Intent().apply {
            action = android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            data = "package:$packageName".toUri()
        }
    }

    override fun parseResult(
        resultCode: Int,
        intent: Intent?
    ): Void? {
        return null
    }
}