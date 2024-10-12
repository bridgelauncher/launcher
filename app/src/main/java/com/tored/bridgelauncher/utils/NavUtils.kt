package com.tored.bridgelauncher.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.tored.bridgelauncher.AppDrawerActivity
import com.tored.bridgelauncher.DevConsoleActivity
import com.tored.bridgelauncher.SettingsActivity

fun Context.startBridgeSettingsActivity(): Unit
{
    startActivity(Intent(this, SettingsActivity::class.java))
}

fun Context.startBridgeAppDrawerActivity(): Unit
{
    startActivity(Intent(this, AppDrawerActivity::class.java))
}

fun Context.startDevConsoleActivity(): Unit
{
    startActivity(Intent(this, DevConsoleActivity::class.java))
}

/** Switch away from Bridge */
fun Context.startAndroidHomeSettingsActivity(): Unit
{
    startActivity(
        Intent(Settings.ACTION_HOME_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    )
}

fun Context.launchViewURIActivity(uriString: String) = launchViewURIActivity(Uri.parse(uriString))
fun Context.launchViewURIActivity(uri: Uri) = startActivity(Intent(Intent.ACTION_VIEW, uri))
