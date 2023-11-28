package com.tored.bridgelauncher.webview

import android.webkit.ConsoleMessage

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
}
