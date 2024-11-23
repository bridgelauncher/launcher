package com.tored.bridgelauncher.ui2.appdrawer.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import com.tored.bridgelauncher.services.apps.InstalledApp
import com.tored.bridgelauncher.services.iconpacks.IconPack

@Composable
fun AppIcon(
    iconPack: IconPack?,
    app: InstalledApp,
    getIconFunc: AppIconGetIconFunc,
    modifier: Modifier = Modifier,
)
{
    val bitmap by rememberAppIconStateWithAutoReload(iconPack, app, getIconFunc)

    val bmp = bitmap
    when (bmp)
    {
        null -> Spacer(
            modifier = modifier
        )

        else -> Image(
            bmp,
            contentDescription = "${app.label} icon",
            modifier = modifier
        )
    }
}