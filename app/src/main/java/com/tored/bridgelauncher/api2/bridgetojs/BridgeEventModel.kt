package com.tored.bridgelauncher.api2.bridgetojs

import kotlinx.serialization.Serializable

@Serializable
abstract class BridgeEventModel(
    override val name: String,
) : IBridgeEventModel
{
    abstract override fun getJson(): String
}

