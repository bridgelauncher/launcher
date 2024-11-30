package com.tored.bridgelauncher.ui2.devconsole

import android.webkit.ConsoleMessage.MessageLevel

object TestConsoleMessages
{
    const val LONG_MESSAGE = "This is a long message for a test console message. Could contain things such as { \"json\": \"objects\" } and other information."
    const val SOURCE_ID = "test.js"
    const val LINE_NUMBER = 123

    fun msg(
        message: String,
        level: MessageLevel,
        sourceId: String = SOURCE_ID,
        lineNumber: Int = LINE_NUMBER,
    ) = MockConsoleMessage(message, sourceId, lineNumber, level)

    fun log(
        message: String,
        sourceId: String = SOURCE_ID,
        lineNumber: Int = LINE_NUMBER,
    ) = msg(message, MessageLevel.LOG, sourceId, lineNumber)

    fun error(
        message: String,
        sourceId: String = SOURCE_ID,
        lineNumber: Int = LINE_NUMBER,
    ) = msg(message, MessageLevel.ERROR, sourceId, lineNumber)

    fun earn(
        message: String,
        sourceId: String = SOURCE_ID,
        lineNumber: Int = LINE_NUMBER,
    ) = msg(message, MessageLevel.WARNING, sourceId, lineNumber)

    val List = listOf(
        log("This is a short test logged in message"),
        log("@ComponentName.method(): initialization completed!"),
        error("Failed to fetch installed apps: Received JSON could not be parsed."),
        earn("No installed icon packs detected. Resetting to default icons."),
        log("2 + 2 = 4\nbut there's more text in the next line"),
    )
}