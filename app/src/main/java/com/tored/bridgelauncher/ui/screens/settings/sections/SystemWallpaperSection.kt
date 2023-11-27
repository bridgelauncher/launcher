package com.tored.bridgelauncher.ui.screens.settings.sections

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.R
import com.tored.bridgelauncher.composables.Btn
import com.tored.bridgelauncher.settings.SettingsState
import com.tored.bridgelauncher.ui.screens.settings.SettingsCheckboxFieldFor
import com.tored.bridgelauncher.ui.screens.settings.SettingsSection

@Composable
fun SettingsSystemWallpaperSection()
{
    val context = LocalContext.current

    SettingsSection(label = "System wallpaper", iconResId = R.drawable.ic_image)
    {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        )
        {
            Btn(
                text = "Change system wallpaper",
                modifier = Modifier
                    .fillMaxWidth(),
                outlined = true,
                onClick = { context.startActivity(Intent(Intent.ACTION_SET_WALLPAPER)) },
            )

            SettingsCheckboxFieldFor(SettingsState::drawSystemWallpaperBehindWebView)
        }
    }
}