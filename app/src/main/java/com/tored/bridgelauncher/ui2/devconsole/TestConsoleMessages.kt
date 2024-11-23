package com.tored.bridgelauncher.ui2.devconsole

import android.webkit.ConsoleMessage.MessageLevel

object TestConsoleMessages
{
    val LONG_MESSAGE = "This is a long message for a test console message. Could contain things such as { \"json\": \"objects\" } and other information."
    val SOURCE_ID = "test.js"
    val LINE_NUMBER = 123

    fun Msg(
        message: String,
        level: MessageLevel,
        sourceId: String = SOURCE_ID,
        lineNumber: Int = LINE_NUMBER,
    ) = MockConsoleMessage(message, sourceId, lineNumber, level)

    fun Log(
        message: String,
        sourceId: String = SOURCE_ID,
        lineNumber: Int = LINE_NUMBER,
    ) = Msg(message, MessageLevel.LOG, sourceId, lineNumber)

    fun Error(
        message: String,
        sourceId: String = SOURCE_ID,
        lineNumber: Int = LINE_NUMBER,
    ) = Msg(message, MessageLevel.ERROR, sourceId, lineNumber)

    fun Warn(
        message: String,
        sourceId: String = SOURCE_ID,
        lineNumber: Int = LINE_NUMBER,
    ) = Msg(message, MessageLevel.WARNING, sourceId, lineNumber)

    val List = listOf(
        Log("This is a short test logged in message"),
        Log("@ComponentName.method(): initialization completed!"),
        Error("Failed to fetch installed apps: Received JSON could not be parsed."),
        Warn("No installed icon packs detected. Resetting to default icons."),
        Log("2 + 2 = 4\nbut there's more text in the next line"),
    )
}