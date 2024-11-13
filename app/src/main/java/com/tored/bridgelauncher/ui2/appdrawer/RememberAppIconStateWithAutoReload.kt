package com.tored.bridgelauncher.ui2.appdrawer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import com.tored.bridgelauncher.services.apps.InstalledApp
import com.tored.bridgelauncher.services.iconpacks.IconPack

@Composable
fun RememberAppIconStateWithAutoReload(
    iconPack: IconPack?,
    app: InstalledApp,
    getIconFunc: suspend (iconPack: IconPack?, app: InstalledApp) -> ImageBitmap,
): State<ImageBitmap?>
{
    val bitmapState = remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(app.lastModifiedNanoTime) {
        bitmapState.value = getIconFunc(iconPack, app)
    }

    return bitmapState
}