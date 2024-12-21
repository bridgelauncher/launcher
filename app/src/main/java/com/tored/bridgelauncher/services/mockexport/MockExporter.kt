package com.tored.bridgelauncher.services.mockexport

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import com.tored.bridgelauncher.api2.server.api.endpoints.apps.AppsGetResp
import com.tored.bridgelauncher.services.apps.LaunchableInstalledAppsHolder
import com.tored.bridgelauncher.services.iconcache.IconCache
import com.tored.bridgelauncher.services.iconpacks2.cache.IconPackCache
import com.tored.bridgelauncher.services.iconpacks2.list.InstalledIconPacksHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

// simplified test code for launching tasks in parallel while counting successes and failures
// https://pl.kotl.in/rV6e9XNhw

class MockExporter(
    private val _apps: LaunchableInstalledAppsHolder,
    private val _iconPacks: InstalledIconPacksHolder,
    private val _iconCache: IconCache,
    private val _iconPackCache: IconPackCache,
)
{
    suspend fun exportToDirectory(directory: File, progress: MutableStateFlow<MockExportProgressState?>)
    {
        if (directory.exists())
            assertTrue(directory.isDirectory, "Specified java.io.File is not a directory.")

        assertTrue(directory.canWrite(), "Can't write to the specified directory.")

        val appMap = _apps.packageNameToInstalledAppMap.value

        assertNotNull(appMap)

        // ensure the specified directory exists
        directory.mkdirs()

        // a supervisor scope doesn't get cancelled if one of its jobs fails
        supervisorScope {

            // mock/apps.json
            launch {
                progress.countJob {
                    val apps = appMap.values.map { it.toSerializable() }
                    val resp = AppsGetResp(apps)
                    val appsFile = File(directory, "apps.json")
                    val appsStr = Json.encodeToString(AppsGetResp.serializer(), resp)
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
            for (app in appMap.values)
            {
                launch {
                    progress.countJob {
                        val icon = _iconCache.getIcon(null, app.packageName, System.nanoTime())
                        saveToPNG(icon, File(defIconsDir, "${app.packageName}.png"))
                    }
                }
            }
        }
    }

    private fun saveToPNG(bmp: ImageBitmap, destinationFile: File)
    {
        destinationFile.outputStream().use { stream ->
            bmp.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 90, stream)
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