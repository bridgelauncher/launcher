package com.tored.bridgelauncher.api2.bridgetojs.events.settings

import com.tored.bridgelauncher.api2.bridgetojs.BridgeEventModel
import com.tored.bridgelauncher.api2.shared.SystemBarAppearanceStringOptions
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class NavigationBarAppearanceChangedEvent(
    val newValue: SystemBarAppearanceStringOptions,
) : BridgeEventModel("navigationBarAppearanceChanged")
{
    override fun getJson() = Json.encodeToString(serializer(), this)
}