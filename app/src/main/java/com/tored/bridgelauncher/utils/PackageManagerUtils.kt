package com.tored.bridgelauncher.utils

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.util.Log

fun PackageManager.tryGetApplicationInfo(packageName: String, flags: Int): ApplicationInfo?
{
    return try
    {
        getApplicationInfo(packageName, flags)
    }
    catch (ex: NameNotFoundException)
    {
        Log.w("tryGetApplicationInfo", "${q(packageName)} not found, returning null")
        null
    }
}

fun PackageManager.canAppHandleIntent(packageName: String, intent: Intent): Boolean
{
    return resolveActivity(intent, 0) != null
}