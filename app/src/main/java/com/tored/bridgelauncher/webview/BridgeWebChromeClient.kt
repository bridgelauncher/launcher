package com.tored.bridgelauncher.webview

import android.content.Intent
import android.net.Uri
import android.os.Message
import android.webkit.ConsoleMessage
import android.webkit.WebView


class BridgeWebChromeClient(
    private val consoleMessageCallback: (ConsoleMessage) -> Boolean
) : AccompanistWebChromeClient()
{
    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean
    {
        if (consoleMessage != null)
        {
            val result = consoleMessageCallback(consoleMessage)
            if (result) return true
        }

        return super.onConsoleMessage(consoleMessage)
    }

    // allow _blank links to open in an external browser: https://stackoverflow.com/a/23431369/6796433
    override fun onCreateWindow(view: WebView, dialog: Boolean, userGesture: Boolean, resultMsg: Message?): Boolean
    {
        val result = view.hitTestResult
        val data = result.extra
        val context = view.context
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(data))
        context.startActivity(browserIntent)
        return false
    }
}
