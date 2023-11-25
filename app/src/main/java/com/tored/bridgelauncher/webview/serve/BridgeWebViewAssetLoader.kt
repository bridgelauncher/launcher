package com.tored.bridgelauncher.webview.serve

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import android.webkit.MimeTypeMap
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import com.tored.bridgelauncher.ui.directorypicker.Directory
import java.io.File
import java.io.FileInputStream
import java.io.IOException

private const val TAG = "ASSETLOADER"

class BridgeWebViewAssetLoader(val context: Context, var projectRoot: Directory?)
{
    fun handle(request: WebResourceRequest): WebResourceResponse?
    {
        if (request.url.host?.lowercase() != "bridge.project")
            return null

        try
        {
            Log.i(TAG, "handle: request received")

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
            mutableMapOf<String, String>("A" to "B"),
            msg.byteInputStream(Charsets.UTF_8),
        )
    }
}

object EncodingStrings
{
    const val UTF8 = "utf-8"
}