package com.tored.bridgelauncher.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import com.tored.bridgelauncher.BridgeLauncherApplication
import com.tored.bridgelauncher.services.apps.InstalledApp

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

fun Context.tryStartWallpaperPickerActivity()
{
    tryStartActivity(Intent(Intent.ACTION_SET_WALLPAPER))
}

fun Context.startWallpaperPickerActivity()
{
    startActivity(Intent(Intent.ACTION_SET_WALLPAPER))
}

fun Context.tryStartActivity(intent: Intent)
{
    try
    {
        startActivity(intent)
    }
    catch (ex: Exception)
    {
        showErrorToast(ex)
    }
}

val Context.bridgeLauncherApplication get() = applicationContext as BridgeLauncherApplication

fun Context.checkCanSetSystemNightMode() = ActivityCompat.checkSelfPermission(this, "android.permission.MODIFY_DAY_NIGHT_MODE") == PackageManager.PERMISSION_GRANTED
    || checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED

//fun Context.checkCanLockScreen() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
//    Context.checkIsAccessibilityServiceEnabled()
//else
//    Context.checkIsDeviceAdminEnabled()