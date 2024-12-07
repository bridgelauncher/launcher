package com.tored.bridgelauncher.api2.bridgetojs.events.settings

import com.tored.bridgelauncher.api2.bridgetojs.BridgeEventModel
import com.tored.bridgelauncher.api2.shared.SystemBarAppearanceStringOptions
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class StatusBarAppearanceChangedEvent(
    val newValue: SystemBarAppearanceStringOptions,
) : BridgeEventModel("statusBarAppearanceChanged")
{
    override fun getJson() = Json.encodeToString(serializer(), this)
}