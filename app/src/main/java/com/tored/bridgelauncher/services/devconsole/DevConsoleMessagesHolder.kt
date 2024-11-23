package com.tored.bridgelauncher.services.devconsole

import android.webkit.ConsoleMessage
import androidx.compose.runtime.mutableStateListOf
import com.tored.bridgelauncher.ui2.devconsole.ConsoleMessageWrapper

private const val MAX_CONSOLE_MESSAGES = 2000

class DevConsoleMessagesHolder
{
    private val _messages = mutableStateListOf<ConsoleMessageWrapper>()
    val messages = _messages as List<ConsoleMessageWrapper>

    fun addMessage(consoleMessage: ConsoleMessage)
    {
        if (_messages.size >= MAX_CONSOLE_MESSAGES)
            _messages.removeFirst()

        _messages.add(ConsoleMessageWrapper(consoleMessage))
    }

    fun clearMessages() = _messages.clear()
}