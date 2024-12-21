package com.tored.bridgelauncher.api2.server.api.endpoints

import android.graphics.Bitmap
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import androidx.compose.ui.graphics.asAndroidBitmap
import com.tored.bridgelauncher.api2.server.HTTPStatusCode
import com.tored.bridgelauncher.api2.server.badRequest
import com.tored.bridgelauncher.api2.server.resolveEnumQueryParam
import com.tored.bridgelauncher.api2.server.stringQueryParamOrNull
import com.tored.bridgelauncher.services.apps.LaunchableInstalledAppsHolder
import com.tored.bridgelauncher.services.iconcache.IconCache
import com.tored.bridgelauncher.services.iconpacks2.list.InstalledIconPacksHolder
import com.tored.bridgelauncher.utils.EncodingStrings
import com.tored.bridgelauncher.utils.RawRepresentable
import com.tored.bridgelauncher.utils.q
import java.io.ByteArrayOutputStream

class AppIconsEndpoint(
    private val _installedApps: LaunchableInstalledAppsHolder,
    private val _iconPacks: InstalledIconPacksHolder,
    private val _iconCache: IconCache,
)
{
    suspend fun handle(req: WebResourceRequest): WebResourceResponse
    {
        val packageName = req.url.stringQueryParamOrNull(QUERY_PACKAGE_NAME)
            ?: throw badRequest("No packageName query parameter.")

        val app = _installedApps.packageNameToInstalledAppMap.value?.get(packageName)
            ?: throw badRequest("No app with package name ${q(packageName)}.")

        val notFoundBehavior = req.url.resolveEnumQueryParam(QUERY_NOT_FOUND_BEHAVIOR)
            ?: IconNotFoundBehaviors.Default

        val iconPackPackageName = req.url.stringQueryParamOrNull(QUERY_ICON_PACK_PACKAGE_NAME)?.trim()

        val iconPack = if (iconPackPackageName.isNullOrEmpty())
        {
            if (notFoundBehavior == IconNotFoundBehaviors.Error)
                throw badRequest("Parameter ${q(QUERY_ICON_PACK_PACKAGE_NAME)} must be passed when ${q(QUERY_NOT_FOUND_BEHAVIOR)} is ${q(notFoundBehavior.rawValue)}.")
            else
                null
        }
        else
        {
            null
            // TODO: obtain icon pack
//            _iconPacks.getIconPacks()[iconPackPackageName]
//                ?: throw notFound("No icon pack with package name ${q(iconPackPackageName)}.")
        }

        // TODO: if icon pack is not null, try to get the icon from it
        val bmp = _iconCache.getIcon(iconPackPackageName, packageName, System.nanoTime())

        // the WebView is responsible for closing this stream
        val readStream = ByteArrayOutputStream().use { writeStream ->
            bmp.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 90, writeStream)
            writeStream.toByteArray().inputStream()
        }

        return WebResourceResponse(
            "image/png",
            EncodingStrings.UTF8,
            HTTPStatusCode.OK.rawValue,
            HTTPStatusCode.OK.name,
            null,
            readStream,
        )
    }

    companion object
    {
        const val QUERY_PACKAGE_NAME = "packageName"
        const val QUERY_NOT_FOUND_BEHAVIOR = "notFoundBehavior"
        const val QUERY_ICON_PACK_PACKAGE_NAME = "iconPackPackageName"
    }

    enum class IconNotFoundBehaviors(override val rawValue: String) : RawRepresentable<String>
    {
        Default("default"),
        Error("error"),
    }
}
