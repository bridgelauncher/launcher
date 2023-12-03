package com.tored.bridgelauncher.webview.serve

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import android.webkit.MimeTypeMap
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import com.tored.bridgelauncher.BridgeLauncherApp
import com.tored.bridgelauncher.SerializableInstalledApp
import com.tored.bridgelauncher.ui.directorypicker.Directory
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileInputStream
import java.io.IOException

private const val TAG = "ReqHandler"

const val BRIDGE_HOST = "bridge.launcher"
const val BRIDGE_API_ROOT = ":"
const val BRIDGE_PROJECT_URL = "https://$BRIDGE_HOST/"
const val BRIDGE_API_ENDPOINT_APPS = "apps"

fun getBridgeApiEndpointURL(endpoint: String) = "https://$BRIDGE_HOST/$BRIDGE_API_ROOT/$endpoint"

object EncodingStrings
{
    const val UTF8 = "utf-8"
}

class BridgeWebViewRequestHandler(private val _context: Context, var projectRoot: Directory?)
{
    private val _bridge = _context.applicationContext as BridgeLauncherApp

    fun handle(request: WebResourceRequest): WebResourceResponse?
    {
        val host = request.url.host?.lowercase()

        if (host != BRIDGE_HOST)
            return null

        Log.i(TAG, "handle: request to host \"$host\" received")

        try
        {
            val path = request.url.path
            val apiPrefix = "/$BRIDGE_API_ROOT/"
            if (path != null && path.startsWith(apiPrefix))
            {
                when (val endpoint = path.substring(apiPrefix.length))
                {
                    BRIDGE_API_ENDPOINT_APPS ->
                    {
                        val serializableApps = _bridge.installedAppsHolder.installedApps.map { it.toSerializable() }
                        val json = Json.encodeToString(ListSerializer(SerializableInstalledApp.serializer()), serializableApps)

                        return WebResourceResponse(
                            "application/json",
                            EncodingStrings.UTF8,
                            HTTPStatusCode.OK.rawValue,
                            HTTPStatusCode.OK.name,
                            null,
                            json.byteInputStream(Charsets.UTF_8),
                        )
                    }

                    else -> return errorResponse(HTTPStatusCode.NotFound, "Invalid endpoint ($endpoint)")
                }
            }
            else
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager())
                {
                    return errorResponse(HTTPStatusCode.ServiceUnavailable, "Bridge does not have the \"Manage All Files\" permission.")
                }

                val projRoot = projectRoot ?: return errorResponse(HTTPStatusCode.ServiceUnavailable, "No project is loaded.")

                val method = request.method
                if (method != "GET" && method != "HEAD")
                {
                    return errorResponse(HTTPStatusCode.MethodNotAllowed, "Unsupported HTTP method. Only GET and HEAD are supported.")
                }

                var requestedPath: String = request.url.path ?: "/"
                Log.i(TAG, "handle: request path: $requestedPath")

                // treat requests to directories as requests to
                if (requestedPath.endsWith("/"))
                    requestedPath += "index.html"

                // build a file from the project root and the requested path
                val file = File(projectRoot, requestedPath)

                // resolve a canonical path to the file to check if it is within the project
                val canonicalFile = try
                {
                    file.canonicalFile
                }
                catch (ex: IOException)
                {
                    return errorResponse(HTTPStatusCode.InternalServerError, "Could not obtain a canonical file for the requested path: $ex")
                }

                if (!canonicalFile.path.startsWith(projRoot.canonicalPath))
                {
                    return errorResponse(HTTPStatusCode.Forbidden, "Requested path lies outside of the current project folder.")
                }

                if (!canonicalFile.exists())
                {
                    return errorResponse(HTTPStatusCode.NotFound, "Requested file was not found.")
                }

                if (!canonicalFile.canRead())
                {
                    return errorResponse(HTTPStatusCode.Forbidden, "Bridge does not have permission to read the requested file.")
                }

                val fis = try
                {
                    FileInputStream(canonicalFile)
                }
                catch (ex: Exception)
                {
                    return errorResponse(HTTPStatusCode.InternalServerError, "Failed to open FileInputStream for file: $ex")
                }

                val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension)

                Log.i(TAG, "handle: responding with OK")

                if (method == "GET")
                {
                    return WebResourceResponse(
                        mimeType,
                        null,
                        HTTPStatusCode.OK.rawValue,
                        HTTPStatusCode.OK.name,
                        null,
                        fis,
                    )
                }
                else
                {
                    assert("HEAD" == method)
                    fis.close()
                    return WebResourceResponse(
                        mimeType,
                        null,
                        HTTPStatusCode.OK.rawValue,
                        HTTPStatusCode.OK.name,
                        null,
                        null
                    )
                }
            }
        }
        catch (ex: Exception)
        {
            Log.e(TAG, "handle: unexpected error:", ex)
            return errorResponse(HTTPStatusCode.InternalServerError, "Unexpected error: $ex")
        }
    }

    private fun errorResponse(code: HTTPStatusCode, msg: String): WebResourceResponse
    {
        return WebResourceResponse(
            "text/plain",
            EncodingStrings.UTF8,
            code.rawValue,
            code.name,
            mutableMapOf<String, String>(),
            msg.byteInputStream(Charsets.UTF_8),
        )
    }
}