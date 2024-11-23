package com.tored.bridgelauncher.webview

import android.webkit.ConsoleMessage
import com.tored.bridgelauncher.services.devconsole.DevConsoleMessagesHolder

class BridgeWebChromeClient(
    private val _consoleMessageHolder: DevConsoleMessagesHolder
) : AccompanistWebChromeClient()
{

    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean
    {
        return if (consoleMessage != null)
        {
            _consoleMessageHolder.addMessage(consoleMessage)
            true
        }
        else
            super.onConsoleMessage(consoleMessage)
    }
}
