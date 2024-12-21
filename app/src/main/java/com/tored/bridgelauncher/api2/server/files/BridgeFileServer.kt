package com.tored.bridgelauncher.api2.server.files

import android.os.Environment
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import com.tored.bridgelauncher.api2.server.HTTPStatusCode
import com.tored.bridgelauncher.api2.server.errorResponse
import com.tored.bridgelauncher.utils.CurrentAndroidVersion
import com.tored.bridgelauncher.utils.q
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

private const val TAG = "BridgeFileServer"

class BridgeFileServer(
    private val _currentProjDir: StateFlow<File?>
)
{
    companion object
    {
        val StartupFileNames = listOf("index.html")
    }

    suspend fun handle(req: WebResourceRequest): WebResourceResponse
    {
        if (CurrentAndroidVersion.supportsScopedStorage() && !Environment.isExternalStorageManager())
            return errorResponse(HTTPStatusCode.ServiceUnavailable, "Bridge does not have the \"Manage All Files\" permission.")

        // TODO: load current project root from settings
        val projectRoot = _currentProjDir.value
            ?: return errorResponse(HTTPStatusCode.ServiceUnavailable, "No project is loaded.")

        val projectRootCanonicalPath = try
        {
            projectRoot.canonicalPath
        }
        catch (ex: IOException)
        {
            return errorResponse(HTTPStatusCode.InternalServerError, "Could not obtain a canonical path for the current project folder.")
        }

        val method = req.method
        if (method != "GET" && method != "HEAD")
        {
            return errorResponse(HTTPStatusCode.MethodNotAllowed, "Unsupported HTTP method ${q(method)}. Only GET and HEAD are supported.")
        }

        Log.i(TAG, "Resolving path from URL path ${q(req.url.path)}")

        val resolvedPath: String = req.url.path ?: "/"


        // build a file from the project root and the requested path
        val file = File(projectRoot, resolvedPath)

        // resolve a canonical path to the file to check if it is within the project
        var canonicalFile = try
        {
            file.canonicalFile
        }
        catch (ex: IOException)
        {
            return errorResponse(HTTPStatusCode.InternalServerError, "Could not obtain a canonical file for the requested path: $ex")
        }

        // check if the file is within the project
        if (!canonicalFile.path.startsWith(projectRootCanonicalPath))
            return errorResponse(HTTPStatusCode.Forbidden, "Requested path lies outside of the current project folder.")

        if (!canonicalFile.exists())
            return errorResponse(HTTPStatusCode.NotFound, "Requested file was not found.")

        if (canonicalFile.isDirectory)
        {
            // treat requests to directories as requests to index.html in that directory
            // TODO: allow other files to be resolved for paths to directories? maybe a custom list?
            for (startupFileName in StartupFileNames)
            {
                val startupFile = File(canonicalFile, startupFileName)
                if (startupFile.exists() && startupFile.canRead())
                {
                    canonicalFile = startupFile
                    break
                }
            }

            // we didn't find a startup file
            if (canonicalFile.isDirectory)
                return errorResponse(HTTPStatusCode.NotFound, "Did not find a startup file in the requested directory. Expected startup file names: ${StartupFileNames.joinToString()}")
        }

        if (!canonicalFile.canRead())
            return errorResponse(HTTPStatusCode.Forbidden, "Bridge does not have permission to read the requested file.")

        return withContext(Dispatchers.IO)
        {
            val fis = try
            {
                canonicalFile.inputStream()
            }
            catch (ex: Exception)
            {
                return@withContext errorResponse(HTTPStatusCode.InternalServerError, "Failed to open FileInputStream for file ${q(canonicalFile.path)}: $ex")
            }

            val mimeType = BetterMimeTypeMap[".${file.extension}"]

//            Log.i(TAG, "Responding with OK")

            if (method == "GET")
            {
                return@withContext WebResourceResponse(
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
                // yes, there is a check for the method being GET or HEAD earlier in the function
                // writing this comment because I already forgot and went looking twice
                assert("HEAD" == method)

                // we've verified that we can open a FileInputStream for the file
                // close the stream because HEAD requests don't expect the file's contents
                fis.close()

                return@withContext WebResourceResponse(
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
}