package com.tored.bridgelauncher.services.iconpacks2.list

import com.tored.bridgelauncher.services.apps.InstalledApp
import com.tored.bridgelauncher.services.apps.SerializableInstalledApp
import kotlinx.serialization.Serializable

data class IconPackInfo(
    val app: InstalledApp,
)
{
    val packageName get() = app.packageName

    fun toSerializable(): SerializableIconPackInfo
    {
        return SerializableIconPackInfo(
            app = app.toSerializable(),
        )
    }
}

@Serializable
data class SerializableIconPackInfo(
    val app: SerializableInstalledApp,
)