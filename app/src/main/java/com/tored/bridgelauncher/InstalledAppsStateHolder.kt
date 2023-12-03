package com.tored.bridgelauncher

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import com.tored.bridgelauncher.ui.dirpicker.Directory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.File
import java.text.Normalizer

@Serializable
data class SerializableInstalledApp(
    val uid: Int,
    val packageName: String,
    var label: String,
)

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
            uid,
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
    private val _pm: PackageManager
)
{
    val installedApps = mutableListOf<InstalledApp>()

    fun loadInstalledApps()
    {
        installedApps.clear()

        val apps = _pm.getInstalledApplications(PackageManager.GET_META_DATA)
        for (app in apps)
        {
            val launchIntent = _pm.getLaunchIntentForPackage(app.packageName)
            if (launchIntent != null)
            {
                installedApps.add(
                    InstalledApp(
                        app.uid,
                        app.packageName,
                        _pm.getApplicationLabel(app).toString(),
                        launchIntent,
                        _pm.getApplicationIcon(app),
                    )
                )
            }
        }

        installedApps.sortBy { it.label }
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
                    val apps = installedApps.map { it.toSerializable() }
                    val appsFile = File(dir, "apps.json")
                    val appsStr = Json.encodeToString(ListSerializer(SerializableInstalledApp.serializer()), apps)
                    appsFile.writeText(appsStr)
                }
            }

            val iconsDir = Directory(dir, "icons")
            iconsDir.mkdir()

            val defIconsDir = Directory(iconsDir, "default")
            defIconsDir.mkdir()

            for (app in installedApps)
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
