package com.tored.bridgelauncher.api2.bridgetojs.events.settings

import com.tored.bridgelauncher.api2.bridgetojs.BridgeEventModel
import com.tored.bridgelauncher.api2.shared.OverscrollEffectsStringOptions
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class OverscrollEffectsChangedEvent(
    val newValue: OverscrollEffectsStringOptions,
) : BridgeEventModel("overscrollEffectsChanged")
{
    override fun getJson() = Json.encodeToString(serializer(), this)
}