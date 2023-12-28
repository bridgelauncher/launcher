package com.tored.bridgelauncher

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import com.tored.bridgelauncher.ui.dirpicker.Directory
import com.tored.bridgelauncher.webview.jsapi.BridgeEventArgs
import com.tored.bridgelauncher.webview.serve.BridgeAPIEndpointAppsResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.File
import java.text.Normalizer

private const val TAG = "InstalledApps"

@Serializable
data class SerializableInstalledApp(
    val packageName: String,
    var label: String,
) : BridgeEventArgs

data class InstalledApp(
    val uid: Int,
    val packageName: String,
    val label: String,
    val launchIntent: Intent,
    val defaultIcon: Drawable,
)
{
    val labelSimplified = simplifyLabel(label)

    fun toSerializable(): SerializableInstalledApp
    {
        return SerializableInstalledApp(
            packageName,
            label,
        )
    }

    companion object
    {
        fun simplifyLabel(label: String): String
        {
            return Normalizer
                .normalize(label.trim(), Normalizer.Form.NFD)
                .replace(Regex("\\p{Mn}+"), "")
                .lowercase()
                .replace(Regex("[^a-z0-9]"), "")
        }
    }
}

class InstalledAppsStateHolder(
    context: Context,
    private val _pm: PackageManager,
)
{
    private val _bridge = context.applicationContext as BridgeLauncherApp
    val installedApps = mutableMapOf<String, InstalledApp>()

    fun loadInstalledApps()
    {
        installedApps.clear()

        _pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .forEach { setAppFromAppInfo(it) }
    }

    private fun setAppFromAppInfo(app: ApplicationInfo): InstalledApp?
    {
        val launchIntent = _pm.getLaunchIntentForPackage(app.packageName)
        if (launchIntent != null)
        {
            val newApp = InstalledApp(
                app.uid,
                app.packageName,
                _pm.getApplicationLabel(app).toString(),
                launchIntent,
                _pm.getApplicationIcon(app),
            )

            installedApps[app.packageName] = newApp

            return newApp
        }
        else
        {
            return null
        }
    }

    fun notifyAppAdded(packageName: String)
    {
        val app = updateAppInfo(packageName)
        if (app != null)
            _bridge.bridgeToJSAPI.appInstalled(app.toSerializable())
    }

    fun notifyAppChanged(packageName: String)
    {
        val app = updateAppInfo(packageName)
        if (app != null)
            _bridge.bridgeToJSAPI.appChanged(app.toSerializable())
    }

    private fun updateAppInfo(packageName: String): InstalledApp?
    {
        return try
        {
            val app = _pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            setAppFromAppInfo(app)
        }
        catch (ex: Exception)
        {
            Log.e(TAG, "updateAppInfo: $packageName", ex)
            null
        }
    }

    fun notifyAppRemoved(packageName: String)
    {
        installedApps.remove(packageName)
        _bridge.bridgeToJSAPI.appRemoved(packageName)
    }


    suspend fun exportToDirectoryAsync(dir: Directory, onJobStarted: (jobCount: Int) -> Unit, onJobFinished: (jobCount: Int) -> Unit)
    {
        coroutineScope {

            var startedJobs = 0
            var completedJobs = 0

            suspend fun notifyAboutJob(job: suspend CoroutineScope.() -> Unit)
            {
                startedJobs++
                onJobStarted(startedJobs)
                try
                {
                    job()
                }
                finally
                {
                    completedJobs++
                    onJobFinished(completedJobs)
                }
            }

            launch {
                notifyAboutJob {
                    val apps = installedApps.values.map { it.toSerializable() }
                    val resp = BridgeAPIEndpointAppsResponse(apps)
                    val appsFile = File(dir, "apps.json")
                    val appsStr = Json.encodeToString(BridgeAPIEndpointAppsResponse.serializer(), resp)
                    appsFile.writeText(appsStr)
                }
            }

            val iconsDir = Directory(dir, "icons")
            iconsDir.mkdir()

            val defIconsDir = Directory(iconsDir, "default")
            defIconsDir.mkdir()

            for (app in installedApps.values)
            {
                launch {
                    notifyAboutJob {
//                        delay(abs(Random.nextLong()) % 3000)
                        val file = File(defIconsDir, "${app.packageName}.png")
                        val bmp = app.defaultIcon.toBitmap(config = Bitmap.Config.ARGB_8888)
                        bmp.compress(Bitmap.CompressFormat.PNG, 90, file.outputStream())
                    }
                }
            }

        }
    }
}
