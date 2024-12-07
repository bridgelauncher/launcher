package com.tored.bridgelauncher.api2.webview

import android.webkit.ConsoleMessage
import com.tored.bridgelauncher.services.devconsole.DevConsoleMessagesHolder
import com.tored.bridgelauncher.webview.AccompanistWebChromeClient

class BridgeWebChromeClient(
    private val _consoleMessageHolder: DevConsoleMessagesHolder,
) : AccompanistWebChromeClient()
{

    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean
    {
        return when (consoleMessage)
        {
            null -> super.onConsoleMessage(null)
            else ->
            {
                _consoleMessageHolder.addMessage(consoleMessage)
                true
            }
        }
    }
}