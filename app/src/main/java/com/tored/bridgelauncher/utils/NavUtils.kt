package com.tored.bridgelauncher.utils

import android.content.Context
import android.content.Intent
import com.tored.bridgelauncher.AppDrawerActivity
import com.tored.bridgelauncher.DevConsoleActivity
import com.tored.bridgelauncher.SettingsActivity

fun Context.startBridgeSettingsActivity(): Unit
{
    startActivity(Intent(this, SettingsActivity::class.java))
}

fun Context.startAppDrawerActivity(): Unit
{
    startActivity(Intent(this, AppDrawerActivity::class.java))
}

fun Context.startDevConsoleActivity(): Unit
{
    startActivity(Intent(this, DevConsoleActivity::class.java))
}

fun Context.startWallpaperPickerActivity(): Unit
{
    startActivity(Intent(Intent.ACTION_SET_WALLPAPER))
}