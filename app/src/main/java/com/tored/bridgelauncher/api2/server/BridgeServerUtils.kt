package com.tored.bridgelauncher.api2.server

import android.net.Uri
import android.webkit.WebResourceResponse
import com.tored.bridgelauncher.utils.EncodingStrings
import com.tored.bridgelauncher.utils.RawRepresentable
import com.tored.bridgelauncher.utils.parseAsEnumOrNull
import com.tored.bridgelauncher.utils.q

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

fun jsonResponse(json: String): WebResourceResponse
{
    return jsonResponse(HTTPStatusCode.OK, json)
}

fun jsonResponse(statusCode: HTTPStatusCode, json: String): WebResourceResponse
{
    return WebResourceResponse(
        "application/json",
        EncodingStrings.UTF8,
        statusCode.rawValue,
        statusCode.name,
        null,
        json.byteInputStream(Charsets.UTF_8),
    )
}

fun errorResponse(code: HTTPStatusCode, msg: String): WebResourceResponse
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


// EXCEPTIONS

class HttpResponseException(val respStatusCode: HTTPStatusCode, val respMessage: String) : Exception(respMessage)

fun badRequest(message: String) = HttpResponseException(HTTPStatusCode.BadRequest, message)
fun notFound(message: String) = HttpResponseException(HTTPStatusCode.NotFound, message)


// QUERY PARAMETERS

fun Uri.stringQueryParamOrNull(name: String): String?
{
    return getQueryParameter(name)
}

/**
 * Tries to parse the value of the query parameter with the given name as an enum option.
 * Returns `null` value if the parameter is not present or if the parameter's value is not one of the enum options.
 */
inline fun <reified TEnum> Uri.resolveEnumQueryParam(name: String): TEnum?
        where TEnum : Enum<TEnum>, TEnum : RawRepresentable<String>
{
    val rawValue = getQueryParameter(name)

    return if (rawValue == null)
        null
    else
        parseAsEnumOrNull(rawValue)
            ?: throw invalidEnumValueBadRequest<TEnum>(name, rawValue)
}

inline fun <reified TEnum> invalidEnumValueBadRequest(paramName: String, rawValue: String): HttpResponseException
        where TEnum : Enum<TEnum>, TEnum : RawRepresentable<String>
{
    return badRequest("Unexpected value for ${q(paramName)}: ${q(rawValue)}. Expected one of: ${enumValues<TEnum>().joinToString { q(it.rawValue) }}")
}
