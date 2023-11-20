package com.tored.bridgelauncher.webview

import android.webkit.ConsoleMessage

class BridgeWebChromeClient(
    onConsoleMessage: (ConsoleMessage) -> Boolean
) : AccompanistWebChromeClient()
{
    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean
    {
        return super.onConsoleMessage(consoleMessage)
    }
}
