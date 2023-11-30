package com.tored.bridgelauncher.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.tored.bridgelauncher.InstalledApp

fun Context.openAppInfo(packageName: String)
{
    startActivity(
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:${packageName}")
        )
    )
}

fun Context.requestAppUninstall(packageName: String)
{
    val packageURI = Uri.parse("package:${packageName}")
    val uninstallIntent = Intent(Intent.ACTION_DELETE, packageURI)
    startActivity(uninstallIntent)
}

fun Context.launchApp(app: InstalledApp)
{
    startActivity(app.launchIntent)
}

fun Context.launchApp(packageName: String)
{
    val intent = packageManager.getLaunchIntentForPackage(packageName)
    if (intent != null)
        startActivity(intent)
    else
        throw Exception("Launch intent not found.")
}

fun Context.startWallpaperPickerActivity()
{
    startActivity(Intent(Intent.ACTION_SET_WALLPAPER))
}
