package com.tored.bridgelauncher.api2.server.api.endpoints.apps

import com.tored.bridgelauncher.services.apps.SerializableInstalledApp
import kotlinx.serialization.Serializable

@Serializable
data class AppsGetResp(
    val apps: List<SerializableInstalledApp>,
)