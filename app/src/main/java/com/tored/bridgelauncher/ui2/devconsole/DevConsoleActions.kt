package com.tored.bridgelauncher.ui2.devconsole

data class DevConsoleActions(
    val clearMessages: () -> Unit,
)
{
    companion object
    {
        fun empty() = DevConsoleActions({})
    }
}