package com.tored.bridgelauncher.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import com.tored.bridgelauncher.InstalledApp


fun Context.tryOpenAppInfo(packageName: String, showToastIfFailed: Boolean = true): Boolean
{
    return tryRun(
        { openAppInfo(packageName) },
        showToastIfFailed,
        "Could not open app info"
    )
}

fun Context.openAppInfo(packageName: String)
{
    startActivity(
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:${packageName}")
        )
    )
}


fun Context.tryRequestAppUninstall(packageName: String, showToastIfFailed: Boolean): Boolean
{
    return tryRun(
        { requestAppUninstall(packageName) },
        showToastIfFailed,
        "Could not request uninstall"
    )
}

fun Context.requestAppUninstall(packageName: String)
{
    val packageURI = Uri.parse("package:${packageName}")
    val uninstallIntent = Intent(Intent.ACTION_DELETE, packageURI)
    startActivity(uninstallIntent)
}


fun Context.tryLaunchApp(app: InstalledApp, showToastIfFailed: Boolean = true): Boolean
{
    return tryRun(
        { launchApp(app) },
        showToastIfFailed,
        "Could not launch app"
    )
}

fun Context.launchApp(app: InstalledApp)
{
    startActivity(app.launchIntent)
}


fun Context.tryLaunchApp(packageName: String, showToastIfFailed: Boolean = true): Boolean
{
    return tryRun(
        { launchApp(packageName) },
        showToastIfFailed,
        "Could not launch app"
    )
}

fun Context.launchApp(packageName: String)
{
    val intent = packageManager.getLaunchIntentForPackage(packageName)
    if (intent != null)
        startActivity(intent)
    else
        throw Exception("Launch intent not found.")
}

private fun Context.tryRun(f: () -> Unit, showToastIfFailed: Boolean, errMsg: String): Boolean
{
    try
    {
        f()
        return true
    }
    catch (ex: Exception)
    {
        if (showToastIfFailed)
            Toast.makeText(this, "$errMsg: ${ex.message}", Toast.LENGTH_SHORT).show()
        return false
    }
}