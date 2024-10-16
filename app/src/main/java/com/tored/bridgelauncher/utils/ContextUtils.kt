package com.tored.bridgelauncher.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import androidx.core.app.ActivityCompat
import com.tored.bridgelauncher.BridgeLauncherApplication

val Context.bridgeLauncherApplication get() = applicationContext as BridgeLauncherApplication

fun Context.checkCanSetSystemNightMode() = ActivityCompat.checkSelfPermission(this, "android.permission.MODIFY_DAY_NIGHT_MODE") == PackageManager.PERMISSION_GRANTED
        || checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED

fun Context.checkStoragePerms(): Boolean
{
    return if (CurrentAndroidVersion.supportsScopedStorage())
    {
        // we need a special permission on Android 11 and up
        Environment.isExternalStorageManager()
    }
    else
    {
        ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }
}

//fun Context.checkCanLockScreen() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
//    Context.checkIsAccessibilityServiceEnabled()
//else
//    Context.checkIsDeviceAdminEnabled()