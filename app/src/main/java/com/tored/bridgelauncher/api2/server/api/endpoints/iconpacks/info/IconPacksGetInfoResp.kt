package com.tored.bridgelauncher.api2.server.api.endpoints.iconpacks.info

import com.tored.bridgelauncher.services.iconpacks2.list.SerializableIconPackInfo
import kotlinx.serialization.Serializable

@Serializable
data class IconPacksGetInfoResp(
    val iconPack: SerializableIconPackInfo,
)