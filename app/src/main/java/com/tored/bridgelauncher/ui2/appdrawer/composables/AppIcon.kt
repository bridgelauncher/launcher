package com.tored.bridgelauncher.ui2.appdrawer.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.services.apps.InstalledApp
import com.tored.bridgelauncher.services.iconpacks.IconPack

@Composable
fun AppIcon(
    iconPack: IconPack?,
    app: InstalledApp,
    getIconFunc: suspend (iconPack: IconPack?, app: InstalledApp) -> ImageBitmap,
)
{
    val bitmap by RememberAppIconStateWithAutoReload(iconPack, app, getIconFunc)

    val bmp = bitmap
    when (bmp)
    {
        null -> Spacer(
            modifier = Modifier.size(48.dp)
        )

        else -> Image(bmp, "${app.label} icon")
    }
}