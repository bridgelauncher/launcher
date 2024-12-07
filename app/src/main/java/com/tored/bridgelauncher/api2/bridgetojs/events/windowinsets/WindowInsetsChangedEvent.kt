package com.tored.bridgelauncher.api2.bridgetojs.events.windowinsets

import com.tored.bridgelauncher.api2.bridgetojs.IBridgeEventModel
import com.tored.bridgelauncher.services.windowinsetsholder.WindowInsetsOptions
import com.tored.bridgelauncher.services.windowinsetsholder.WindowInsetsSnapshot
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
class WindowInsetsChangedEvent(
    override val name: String,
    val insets: WindowInsetsSnapshot,
) : IBridgeEventModel
{
    override fun getJson() = Json.encodeToString(this)

    companion object
    {
        fun fromSnapshot(option: WindowInsetsOptions, snapshot: WindowInsetsSnapshot) = WindowInsetsChangedEvent(
            name = "${option.name}WindowInsetsChanged",
            insets = snapshot,
        )
    }
}