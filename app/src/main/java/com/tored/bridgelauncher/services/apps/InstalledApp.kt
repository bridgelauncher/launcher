package com.tored.bridgelauncher.services.apps

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.tored.bridgelauncher.ui2.appdrawer.IAppDrawerApp
import com.tored.bridgelauncher.utils.tryGetApplicationInfo
import kotlinx.serialization.Serializable
import java.text.Normalizer

data class InstalledApp(
    val appInfo: ApplicationInfo,
    val launchIntent: Intent?,
    override val label: String,
) : IAppDrawerApp
{
    override val packageName: String get() = appInfo.packageName
    val uid get() = appInfo.uid

    val lastModifiedNanoTime = System.nanoTime()
    val labelSimplified = simplifyLabel(label)

    fun toSerializable(): SerializableInstalledApp
    {
        return SerializableInstalledApp(
            packageName = packageName,
            label = label,
        )
    }

    companion object
    {
        fun simplifyLabel(label: String): String
        {
            return Normalizer.normalize(label.trim(), Normalizer.Form.NFD)
                .replace(Regex("\\p{Mn}+"), "")
                .lowercase()
                .replace(Regex("[^a-z0-9]"), "")
        }
    }
}


fun PackageManager.tryLoadInstalledAppIfLaunchable(packageName: String): InstalledApp?
{
    return tryGetApplicationInfo(packageName, PackageManager.GET_META_DATA)?.let { tryLoadInstalledAppIfLaunchable(it) }
}

fun PackageManager.tryLoadInstalledApp(packageName: String): InstalledApp?
{
    return tryGetApplicationInfo(packageName, PackageManager.GET_META_DATA)?.let { loadInstalledApp(it) }
}

fun PackageManager.tryLoadInstalledAppIfLaunchable(
    appInfo: ApplicationInfo,
): InstalledApp?
{
    return getLaunchIntentForPackage(appInfo.packageName)?.let { launchIntent ->
        loadInstalledApp(appInfo, launchIntent)
    }
}

fun PackageManager.loadInstalledApp(appInfo: ApplicationInfo): InstalledApp
{
    return loadInstalledApp(appInfo, getLaunchIntentForPackage(appInfo.packageName))
}

fun PackageManager.loadInstalledApp(
    appInfo: ApplicationInfo,
    launchIntent: Intent?,
): InstalledApp
{
    return InstalledApp(
        appInfo = appInfo,
        launchIntent = launchIntent,
        label = getApplicationLabel(appInfo).toString(),
    )
}

@Serializable
data class SerializableInstalledApp(
    val packageName: String,
    var label: String,
)