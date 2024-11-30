package com.tored.bridgelauncher.api2.bridgetojs.events.apps

import com.tored.bridgelauncher.api2.bridgetojs.BridgeEventModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class AppRemovedEvent(
    val packageName: String,
) : BridgeEventModel("appRemoved")
{
    override fun getJson() = Json.encodeToString(serializer(), this)
}