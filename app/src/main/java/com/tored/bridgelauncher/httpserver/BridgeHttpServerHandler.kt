package com.tored.bridgelauncher.httpserver

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import android.webkit.MimeTypeMap
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.tored.bridgelauncher.ui.directorypicker.Directory
import com.tored.bridgelauncher.utils.RawRepresentable
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


enum class HTTPStatusCode(override val rawValue: Int) : RawRepresentable<Int>
{
    OK(200),
    BadRequest(400),
    Forbidden(403),
    NotFound(404),
    MethodNotAllowed(405),
    InternalServerError(500),
    ServiceUnavailable(503),
    NotImplemented(501),
}

// implemented with help from
// https://stackoverflow.com/a/42404471/6796433
class BridgeHttpServerHandler(
    val context: Context,
    var projectRoot: Directory? = null
) : HttpHandler
{
    private val TAG = "HTTPSERVER"

    override fun handle(exchange: HttpExchange)
    {
        try
        {
            Log.i(TAG, "handle: request received")

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager())
            {
                exchange.sendError(HTTPStatusCode.ServiceUnavailable, "Bridge does not have the \"Manage All Files\" permission.")
                return
            }

            val projRoot = projectRoot
            if (projRoot == null)
            {
                exchange.sendError(HTTPStatusCode.ServiceUnavailable, "No project is loaded.")
                return
            }

            val method = exchange.requestMethod
            if (method != "GET" && method != "HEAD")
            {
                exchange.sendError(HTTPStatusCode.MethodNotAllowed, "Unsupported HTTP method. Only GET and HEAD are supported.")
                return
            }

            var requestedPath: String = exchange.requestURI.path
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
                exchange.sendError(HTTPStatusCode.InternalServerError, "Could not obtain a canonical file for the requested path: $ex")
                return
            }

            if (!canonicalFile.path.startsWith(projRoot.canonicalPath))
            {
                exchange.sendError(HTTPStatusCode.Forbidden, "Requested path lies outside of the current project folder.")
                return
            }

            if (!canonicalFile.exists())
            {
                exchange.sendError(HTTPStatusCode.NotFound, "Requested file was not found.")
                return
            }

            if (!canonicalFile.canRead())
            {
                exchange.sendError(HTTPStatusCode.Forbidden, "Bridge does not have permission to read the requested file.")
                return
            }

            try
            {
                FileInputStream(canonicalFile)
            }
            catch (ex: Exception)
            {
                exchange.sendError(HTTPStatusCode.InternalServerError, "Failed to open FileInputStream for file: $ex")
                return
            }
                .use()
                { fis ->
                    val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension)
                    exchange.responseHeaders.set("Content-Type", mimeType)

                    Log.i(TAG, "handle: responding with OK")

                    if (method == "GET")
                    {
                        exchange.sendResponseHeaders(HTTPStatusCode.OK.rawValue, canonicalFile.length())

                        copyStream(fis, exchange.responseBody)
                        exchange.responseBody.close()
                    }
                    else
                    {
                        assert("HEAD" == method)
                        exchange.sendResponseHeaders(HTTPStatusCode.OK.rawValue, -1)
                    }
                }

        }
        catch (ex: Exception)
        {
            try
            {
                Log.e(TAG, "handle: unexpected error:", ex)
                exchange.sendError(HTTPStatusCode.InternalServerError, desc = "Unexpected error: $ex")
            }
            catch (ex: Exception)
            {
                Log.e(TAG, "handle: Could not send InternalServerError:", ex)
            }
        }
    }

    private fun HttpExchange.sendError(code: HTTPStatusCode, desc: String)
    {
        Log.i(TAG, "handle: responding with error $code: $desc")

        val message = "HTTP error ${code.rawValue}: $desc"
        val messageBytes = message.toByteArray(charset("UTF-8"))

        responseHeaders.set("Content-Type", "text/plain; charset=utf-8")
        sendResponseHeaders(code.rawValue, messageBytes.size.toLong())

        responseBody.write(messageBytes)
        responseBody.close()
    }

    @Throws(IOException::class)
    private fun copyStream(src: InputStream, dest: OutputStream)
    {
        val buf = ByteArray(4096)
        var blockLength: Int
        while (src.read(buf).also { blockLength = it } >= 0)
        {
            dest.write(buf, 0, blockLength)
        }
    }
}
