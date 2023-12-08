package com.tored.bridgelauncher.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.tored.bridgelauncher.BridgeLauncherApp

class BridgeLauncherBroadcastReceiver : BroadcastReceiver()
{
    override fun onReceive(context: Context?, intent: Intent?)
    {
        if (context == null || intent == null) return

        val bridge = context.applicationContext as BridgeLauncherApp

        when (intent.action)
        {
            Intent.ACTION_PACKAGE_ADDED ->
            {
                val packageName = intent.dataString
                val isReplacing = intent.extras?.getBoolean(Intent.EXTRA_REPLACING) ?: false

                if (packageName != null && !isReplacing)
                {
                    bridge.installedAppsHolder.notifyAppAdded(packageName)
                }
            }

            Intent.ACTION_PACKAGE_CHANGED,
            Intent.ACTION_PACKAGE_REPLACED ->
            {
                val packageName = intent.dataString
                if (packageName != null)
                {
                    bridge.installedAppsHolder.notifyAppChanged(packageName)
                }
            }

            Intent.ACTION_PACKAGE_REMOVED ->
            {
                val packageName = intent.dataString
                val isReplacing = intent.extras?.getBoolean(Intent.EXTRA_REPLACING) ?: false
                if (packageName != null && !isReplacing)
                {
                    bridge.installedAppsHolder.notifyAppRemoved(packageName)
                }
            }

        }
    }

    companion object
    {
        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
        }
    }
}