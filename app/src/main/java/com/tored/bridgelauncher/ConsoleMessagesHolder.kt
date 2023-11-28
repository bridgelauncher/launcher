package com.tored.bridgelauncher

import android.webkit.ConsoleMessage
import androidx.compose.runtime.mutableStateListOf

class ConsoleMessagesHolder
{
    val messages = mutableStateListOf<ConsoleMessage>()
}