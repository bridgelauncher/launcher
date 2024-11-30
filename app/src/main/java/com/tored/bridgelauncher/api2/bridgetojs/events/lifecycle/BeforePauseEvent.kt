package com.tored.bridgelauncher.api2.bridgetojs.events.lifecycle

import com.tored.bridgelauncher.api2.bridgetojs.BridgeEventModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class BeforePauseEvent : BridgeEventModel("beforePause")
{
    override fun getJson() = Json.encodeToString(serializer(), this)
}