package com.tored.bridgelauncher

import android.webkit.ConsoleMessage
import androidx.compose.runtime.mutableStateListOf

class ConsoleMessagesHolder
{
    private val _messages = mutableStateListOf<ConsoleMessage>()
    val messages: List<ConsoleMessage> = _messages;

    fun addMessage(consoleMessage: ConsoleMessage)
    {
        if (_messages.size >= 2000)
            _messages.removeFirst()

        _messages.add(consoleMessage)
    }

    fun clearMessages() = _messages.clear()
}