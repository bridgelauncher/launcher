package com.tored.bridgelauncher.api2.bridgetojs.events.settings

import com.tored.bridgelauncher.api2.bridgetojs.BridgeEventModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class DrawSystemWallpaperBehindWebViewChangedEvent(
    val newValue: Boolean,
) : BridgeEventModel("drawSystemWallpaperBehindWebViewChanged")
{
    override fun getJson() = Json.encodeToString(serializer(), this)
}