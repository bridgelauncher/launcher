package com.tored.bridgelauncher.httpserver

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.documentfile.provider.DocumentFile
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.tored.bridgelauncher.utils.RawRepresentable
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


enum class HTTPStatusCode(override val rawValue: Int) : RawRepresentable<Int>
{
    OK(200),
    BadRequest(400),
    NotFound(404),
    InternalServerError(500),
    ServiceUnavailable(503),
    NotImplemented(501),
}

// implemented with help from
// https://stackoverflow.com/a/42404471/6796433
class BridgeHttpServerHandler(
    val context: Context,
    var projectRoot: Uri? = null
) : HttpHandler
{
    val contentResolver: ContentResolver = context.contentResolver
    val TAG = "HTTPSERVER"

    override fun handle(exchange: HttpExchange)
    {
        try
        {
            Log.d(TAG, "handle: start")

            val projRoot = projectRoot
            if (projRoot == null)
            {
                exchange.sendError(HTTPStatusCode.ServiceUnavailable, "No project is loaded.")
                return
            }

            val method = exchange.requestMethod
            if (method != "GET" && method != "HEAD")
            {
                exchange.sendError(HTTPStatusCode.NotImplemented, "Unsupported HTTP method. Only GET and HEAD are supported.")
                return
            }

            val requestedPath: String = exchange.requestURI.path
            if (requestedPath.endsWith("/") && requestedPath != "/")
            {
                exchange.sendError(HTTPStatusCode.NotImplemented, "Directory serving is not implemented.")
            }

            Log.d(TAG, "handle: proj root: $projRoot")
            Log.d(TAG, "handle: requested path: $requestedPath")

            assert(DocumentsContract.isTreeUri(projRoot))

            val df = DocumentFile.fromTreeUri(context, projRoot)
            val file = df!!.findFile(
                if (requestedPath == "/")
                    "index.html"
                else
                    requestedPath.replace(Regex("^/"), "")
            )

            val builtDocUri = file!!.uri

//            DocumentsContract.buildChildDocumentsUriUsingTree()

//                DocumentsContract.buildDocumentUriUsingTree(
//                projRoot,
//                DocumentsContract.getTreeDocumentId(
//                    projRoot,

//                )
//            )

            Log.d(TAG, "handle: built doc uri: $builtDocUri")

            val len = contentResolver
                .openFileDescriptor(builtDocUri, "r")
                .use {
                    if (it == null)
                    {
                        Log.e(TAG, "handle: openFileDescriptor returned null")
                        exchange.sendError(HTTPStatusCode.InternalServerError, "Could not open a FileDescriptior for the requested file.")
                    }
                    else
                    {
                        it.statSize
                        FileInputStream(it.fileDescriptor).use()
                        { fis ->
                            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(file.name))
                            exchange.responseHeaders.set("Content-Type", mimeType)

                            if (method == "GET")
                            {
                                exchange.sendResponseHeaders(HTTPStatusCode.OK.rawValue, it.statSize)

                                copyStream(fis, exchange.responseBody)
                                exchange.responseBody.close()
                            }
                            else
                            {
                                assert("HEAD" == method)
                                exchange.sendResponseHeaders(HTTPStatusCode.OK.rawValue, -1)
                            }

                            Log.d(TAG, "handle: OK")
                        }
                    }
                }

//            val file = File(projectRoot, requestedPath)
//
//            // resolve a canonical path to the file to check if it is within the project
//            val canonicalFile = try
//            {
//                file.canonicalFile
//            }
//            catch (ex: IOException)
//            {
//                exchange.reportPathTraversal()
//                return
//            }
//
//            if (!canonicalFile.path.startsWith(projRoot.path))
//            {
//                exchange.reportPathTraversal()
//                return
//            }
//
//            val fis = try
//            {
//                FileInputStream(canonicalFile)
//            }
//            catch (ex: Exception)
//            {
//                exchange.sendError(HTTPStatusCode.NotFound, "Could not open FileInputStream: ${ex.message}")
//                return
//            }
//
//            fis.use()
//            {
//
//            }
        }
        catch (ex: Exception)
        {
            try
            {
                Log.e(TAG, "handle:", ex)
                exchange.sendError(HTTPStatusCode.InternalServerError, "Something went wrong: ${ex.message}")
            }
            catch (ex: Exception)
            {
                println("Could not send InternalServerError: ${ex.message}")
            }
        }
    }

    private fun HttpExchange.sendError(code: HTTPStatusCode, desc: String)
    {
        val message = "HTTP error ${code.rawValue}: $desc"
        val messageBytes = message.toByteArray(charset("UTF-8"))

        responseHeaders.set("Content-Type", "text/plain; charset=utf-8")
        sendResponseHeaders(code.rawValue, messageBytes.size.toLong())

        responseBody.write(messageBytes)
        responseBody.close()
    }

    private fun HttpExchange.reportPathTraversal()
    {
        sendError(HTTPStatusCode.BadRequest, "Attempt to access a file from outiside the active project detected.")
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
