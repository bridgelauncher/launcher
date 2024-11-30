package com.tored.bridgelauncher.api2.server

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse

interface IBridgeServerEndpoint
{
    suspend fun handle(req: WebResourceRequest): WebResourceResponse
}
