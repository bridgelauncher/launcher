package com.tored.bridgelauncher.api.server.endpoints

import android.graphics.Bitmap
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import androidx.core.graphics.drawable.toBitmap
import com.tored.bridgelauncher.api.server.HTTPStatusCode
import com.tored.bridgelauncher.api.server.IBridgeServerEndpoint
import com.tored.bridgelauncher.api.server.badRequest
import com.tored.bridgelauncher.api.server.notFound
import com.tored.bridgelauncher.api.server.resolveEnumQueryParam
import com.tored.bridgelauncher.api.server.stringQueryParamOrNull
import com.tored.bridgelauncher.services.apps.InstalledAppsHolder
import com.tored.bridgelauncher.services.iconpacks.InstalledIconPacksHolder
import com.tored.bridgelauncher.utils.EncodingStrings
import com.tored.bridgelauncher.utils.RawRepresentable
import com.tored.bridgelauncher.utils.q
import java.io.ByteArrayOutputStream

class AppIconsEndpoint(
    private val _installedApps: InstalledAppsHolder,
    private val _iconPacks: InstalledIconPacksHolder,
) : IBridgeServerEndpoint
{
    override suspend fun handle(req: WebResourceRequest): WebResourceResponse
    {
        val packageName = req.url.stringQueryParamOrNull(QUERY_PACKAGE_NAME)
            ?: throw badRequest("No packageName query parameter.")

        val app = _installedApps.packageNameToInstalledAppMap[packageName]
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
            _iconPacks.getIconPacks()[iconPackPackageName]
                ?: throw notFound("No icon pack with package name ${q(iconPackPackageName)}.")
        }

        // TODO: if icon pack is not null, try to get the icon from it
        val bmp = app.defaultIcon.toBitmap()

        // the WebView is responsible for closing this stream
        val readStream = ByteArrayOutputStream().use { writeStream ->
            bmp.compress(Bitmap.CompressFormat.PNG, 90, writeStream)
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
