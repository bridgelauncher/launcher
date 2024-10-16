package com.tored.bridgelauncher.utils

import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import com.tored.bridgelauncher.AppDrawerActivity
import com.tored.bridgelauncher.DevConsoleActivity
import com.tored.bridgelauncher.services.apps.InstalledApp
import com.tored.bridgelauncher.ui2.settings.SettingsScreenActivity

fun Context.startBridgeSettingsActivity() = startActivity(Intent(this, SettingsScreenActivity::class.java))
fun Context.tryStartBridgeSettingsActivity() = tryOrShowErrorToast { startBridgeSettingsActivity() }

fun Context.startBridgeAppDrawerActivity() = startActivity(Intent(this, AppDrawerActivity::class.java))
fun Context.tryStartBridgeAppDrawerActivity() = tryOrShowErrorToast { startBridgeAppDrawerActivity() }

fun Context.startDevConsoleActivity() = startActivity(Intent(this, DevConsoleActivity::class.java))
fun Context.tryStartDevConsoleActivity() = tryOrShowErrorToast { startDevConsoleActivity() }


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
    tryOrShowErrorToast { startWallpaperPickerActivity() }
}

fun Context.startWallpaperPickerActivity()
{
    startActivity(Intent(Intent.ACTION_SET_WALLPAPER))
}

/** Switch away from Bridge */
fun Context.tryStartAndroidHomeSettingsActivity()
{
    tryStartActivity(
        Intent(Settings.ACTION_HOME_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
}

fun Context.tryStartExtStorageManagerPermissionActivity()
{
    if (CurrentAndroidVersion.supportsScopedStorage() && !Environment.isExternalStorageManager())
    {
        tryStartActivity(
            Intent(
                Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                Uri.parse("package:${packageName}")
            )
        )
    }
}

fun Context.tryStartAndroidAccessibilitySettingsActivity()
{
    tryStartActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
}

fun Context.tryStartAndroidAddDeviceAdminActivity()
{
    tryStartActivity(
        Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
            putExtra(
                DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                bridgeLauncherApplication.adminReceiverComponentName
            )
            putExtra(
                DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "Bridge Launcher needs this permission so projects can request the screen to be locked."
            )
        }
    )
}


fun Context.launchViewURIActivity(uriString: String) = launchViewURIActivity(Uri.parse(uriString))
fun Context.launchViewURIActivity(uri: Uri) = startActivity(Intent(Intent.ACTION_VIEW, uri))

fun Context.tryStartActivity(intent: Intent) = tryOrShowErrorToast {
    startActivity(intent)
}

fun Context.tryOrShowErrorToast(action: Context.() -> Unit)
{
    try
    {
        action()
    }
    catch (ex: Exception)
    {
        showErrorToast(ex)
    }
}
