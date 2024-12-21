package com.tored.bridgelauncher.services.system

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.tored.bridgelauncher.services.pkgevents.PackageEventsHolder

private val TAG = "BroadcastReceiver"

class BridgeLauncherBroadcastReceiver(
    private val _packageEventsHolder: PackageEventsHolder
) : BroadcastReceiver()
{
    override fun onReceive(context: Context?, intent: Intent?)
    {
        if (context == null || intent == null) return

        Log.d(TAG, "onReceive: ${intent.action}")

        when (intent.action)
        {
            Intent.ACTION_PACKAGE_ADDED ->
            {
                val packageName = intent.data?.encodedSchemeSpecificPart
                val isReplacing = intent.extras?.getBoolean(Intent.EXTRA_REPLACING) ?: false

                if (packageName != null && !isReplacing)
                {
                    _packageEventsHolder.notifyPackageAdded(packageName)
                }
            }

            Intent.ACTION_PACKAGE_REPLACED ->
            {
                val packageName = intent.data?.encodedSchemeSpecificPart
                if (packageName != null)
                {
                    _packageEventsHolder.notifyPackageReplaced(packageName)
                }
            }

            Intent.ACTION_PACKAGE_REMOVED ->
            {
                val packageName = intent.data?.encodedSchemeSpecificPart
                val isReplacing = intent.extras?.getBoolean(Intent.EXTRA_REPLACING) ?: false
                if (packageName != null && !isReplacing)
                {
                    _packageEventsHolder.notifyPackageRemoved(packageName)
                }
            }

        }
    }

    companion object
    {
        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addDataScheme("package")
        }
    }
}

