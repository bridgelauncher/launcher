package com.tored.bridgelauncher.api2.bridgetojs.events.settings

import com.tored.bridgelauncher.api2.bridgetojs.BridgeEventModel
import com.tored.bridgelauncher.api2.shared.BridgeThemeStringOptions
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class BridgeThemeChangedEvent(
    val newValue: BridgeThemeStringOptions,
) : BridgeEventModel("bridgeThemeChanged")
{
    override fun getJson() = Json.encodeToString(serializer(), this)
}