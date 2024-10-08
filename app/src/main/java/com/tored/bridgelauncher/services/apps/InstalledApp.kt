package com.tored.bridgelauncher.services.apps

import android.content.Intent
import android.graphics.drawable.Drawable
import com.tored.bridgelauncher.api.jsapi.BridgeEventArgs
import kotlinx.serialization.Serializable
import java.text.Normalizer

data class InstalledApp(
    val uid: Int,
    val packageName: String,
    val label: String,
    val launchIntent: Intent,
    val defaultIcon: Drawable,
)
{
    val labelSimplified = simplifyLabel(label)

    fun toSerializable(): SerializableInstalledApp
    {
        return SerializableInstalledApp(
            packageName,
            label,
        )
    }

    companion object
    {
        fun simplifyLabel(label: String): String
        {
            return Normalizer.normalize(label.trim(), Normalizer.Form.NFD)
                .replace(Regex("\\p{Mn}+"), "")
                .lowercase()
                .replace(Regex("[^a-z0-9]"), "")
        }
    }
}

@Serializable
data class SerializableInstalledApp(
    val packageName: String,
    var label: String,
) : BridgeEventArgs