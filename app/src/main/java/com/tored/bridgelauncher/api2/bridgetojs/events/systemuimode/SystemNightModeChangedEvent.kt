package com.tored.bridgelauncher.api2.bridgetojs.events.systemuimode

import com.tored.bridgelauncher.api2.bridgetojs.BridgeEventModel
import com.tored.bridgelauncher.api2.shared.SystemNightModeStringOptions
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class SystemNightModeChangedEvent(
    val newValue: SystemNightModeStringOptions,
)
    : BridgeEventModel("systemNightModeChanged")
{
    override fun getJson() = Json.encodeToString(serializer(), this)
}