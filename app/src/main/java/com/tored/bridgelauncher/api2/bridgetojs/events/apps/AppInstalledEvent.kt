package com.tored.bridgelauncher.api2.bridgetojs.events.apps

import com.tored.bridgelauncher.api2.bridgetojs.BridgeEventModel
import com.tored.bridgelauncher.services.apps.SerializableInstalledApp
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class AppInstalledEvent(
    val app: SerializableInstalledApp,
) : BridgeEventModel("appInstalled")
{
    override fun getJson() = Json.encodeToString(serializer(), this)
}