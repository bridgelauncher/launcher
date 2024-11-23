package com.tored.bridgelauncher.ui2.devconsole

import android.webkit.ConsoleMessage.MessageLevel
import com.tored.bridgelauncher.api.server.BridgeServer
import java.util.concurrent.atomic.AtomicLong

private var IConsoleMessageCounter = AtomicLong(0)

abstract class IConsoleMessage
{
    val uid: Long = IConsoleMessageCounter.getAndIncrement()
    abstract val message: String?
    abstract val sourceId: String?
    abstract val lineNumber: Int
    abstract val messageLevel: MessageLevel?
}

data class MockConsoleMessage(
    override val message: String?,
    override val sourceId: String?,
    override val lineNumber: Int,
    override val messageLevel: MessageLevel?,
) : IConsoleMessage()


data class ConsoleMessageWrapper(
    val msg: android.webkit.ConsoleMessage,
): IConsoleMessage()
{
    override val message: String? get() = msg.message()
    override val sourceId: String? get() = msg.sourceId()
    override val lineNumber: Int get() = msg.lineNumber()
    override val messageLevel: MessageLevel? get() = msg.messageLevel()
}

fun IConsoleMessage.getSourceAndLineString(): String
{
    var source = sourceId

    if (source?.startsWith(BridgeServer.PROJECT_URL) == true)
        source = source.substringAfter(BridgeServer.PROJECT_URL)

    return "${source}:${lineNumber}"
}