package com.tored.bridgelauncher.api2.bridgetojs.events.permissions

import com.tored.bridgelauncher.api2.bridgetojs.BridgeEventModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class CanRequestSystemNightModeChangedEvent(
    val newValue: Boolean,
): BridgeEventModel("canRequestSystemNightModeChanged")
{
    override fun getJson() = Json.encodeToString(serializer(), this)
}