package com.tored.bridgelauncher.services.mockexport

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import com.tored.bridgelauncher.api2.server.BridgeAPIEndpointAppsResponse
import com.tored.bridgelauncher.services.apps.InstalledAppsHolder
import com.tored.bridgelauncher.services.iconpackcache.InstalledIconPacksHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.test.assertTrue

// simplified test code for launching tasks in parallel while counting successes and failures
// https://pl.kotl.in/rV6e9XNhw

class MockExporter(
    private val _apps: InstalledAppsHolder,
    private val _iconPacks: InstalledIconPacksHolder,
)
{
    suspend fun exportToDirectory(directory: File, progress: MutableStateFlow<MockExportProgressState?>)
    {
        if (directory.exists())
            assertTrue(directory.isDirectory, "Specified java.io.File is not a directory.")

        assertTrue(directory.canWrite(), "Can't write to the specified directory.")

        // ensure the specified directory exists
        directory.mkdirs()

        // a supervisor scope doesn't get cancelled if one of its jobs fails
        supervisorScope {

            // mock/apps.json
            launch {
                progress.countJob {
                    val apps = _apps.packageNameToInstalledAppMap.values.map { it.toSerializable() }
                    val resp = BridgeAPIEndpointAppsResponse(apps)
                    val appsFile = File(directory, "apps.json")
                    val appsStr = Json.encodeToString(BridgeAPIEndpointAppsResponse.serializer(), resp)
                    appsFile.writeText(appsStr)
                }
            }

            // mock/icons
            val iconsDir = File(directory, "icons")
            iconsDir.mkdir()

            // mock/icons/default
            val defIconsDir = File(iconsDir, "default")
            defIconsDir.mkdir()

            // mock/icons/default/com.package.name.png
            for (app in _apps.packageNameToInstalledAppMap.values)
            {
                launch {
                    progress.countJob {
                        saveDrawableToPNG(app.defaultIcon, File(defIconsDir, "${app.packageName}.png"))
                    }
                }
            }
        }
    }

    private fun saveDrawableToPNG(drawable: Drawable, destinationFile: File)
    {
        destinationFile.outputStream().use { stream ->
            drawable.toBitmap(config = Bitmap.Config.ARGB_8888)
                .compress(Bitmap.CompressFormat.PNG, 90, stream)
        }
    }
}

data class MockExportProgressState(
    val jobsToDo: Int = 0,
    val jobsDone: Int = 0,
    val jobsFailed: Int = 0,
)

val MockExportProgressState.hasFinished: Boolean
    get() = jobsToDo > 0 && jobsDone + jobsFailed >= jobsToDo

suspend fun MutableStateFlow<MockExportProgressState?>.countJob(job: suspend () -> Unit)
{
    update { it!!.copy(jobsToDo = it.jobsToDo + 1) }
    try
    {
        job()
        update { it!!.copy(jobsDone = it.jobsDone + 1) }
    }
    catch (ex: Exception)
    {
        update { it!!.copy(jobsFailed = it.jobsFailed + 1) }
        throw ex
    }
}