package com.tored.bridgelauncher.ui.screens.settings.sections

import androidx.compose.runtime.Composable
import com.tored.bridgelauncher.R
import com.tored.bridgelauncher.composables.Btn
import com.tored.bridgelauncher.ui.screens.settings.SettingsSection
import com.tored.bridgelauncher.ui.shared.ActionCard

@Composable
fun SettingsDevelopmentSection()
{
    SettingsSection(label = "Development", iconResId = R.drawable.ic_tools)
    {
        ActionCard(
            title = "Bridge developer hub",
            description = "Documentation and tools to help you develop Bridge Launcher projects."
        )
        {
            Btn(text = "Open in browser", suffixIcon = R.drawable.ic_open_in_new, onClick = { /* TODO */ })
        }

        ActionCard(
            title = "Export installed apps",
            description = "Create a folder with information about apps installed on this phone, including icons. You can use this folder to work on projects from your PC."
        )
        {
            Btn(text = "Export", suffixIcon = R.drawable.ic_save_to_device, onClick = { /* TODO */ })
        }
    }
}