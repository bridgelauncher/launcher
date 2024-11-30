package com.tored.bridgelauncher.api2.bridgetojs.events.settings

import com.tored.bridgelauncher.api2.bridgetojs.BridgeEventModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class BridgeButtonVisibilityChangedEvent(
    val newValue: com.tored.bridgelauncher.api2.shared.BridgeButtonVisibilityStringOptions,
) : BridgeEventModel("bridgeButtonVisibilityChanged")
{
    override fun getJson() = Json.encodeToString(serializer(), this)
}