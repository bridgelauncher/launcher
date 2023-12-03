package com.tored.bridgelauncher.ui.screens.settings.sections

import androidx.compose.runtime.Composable
import com.tored.bridgelauncher.R
import com.tored.bridgelauncher.composables.Btn
import com.tored.bridgelauncher.ui.screens.settings.SettingsSection
import com.tored.bridgelauncher.ui.shared.ActionCard

@Composable
fun SettingsDevelopmentSection(onExportAppsRequest: () -> Unit)
{
    SettingsSection(label = "Development", iconResId = R.drawable.ic_tools)
    {
        ActionCard(
            title = "Bridge Developer Hub",
            description = "Documentation and tools to help you develop Bridge Launcher projects."
        )
        {
            Btn(text = "Open in browser", suffixIcon = R.drawable.ic_open_in_new, onClick = { /* TODO */ })
        }

        ActionCard(
            title = "Export installed apps",
            descriptionParagraphs = listOf(
                "You will be prompted to select a directory. "
                        + "The export will contain a list of apps installed on this device and their icons.",

                "These files help mock the Bridge JS to Android API for development purposes.\n"
                        + "For more information, please refer to the Bridge Developer Hub.",
            )
        )
        {
            Btn(
                text = "Export",
                suffixIcon = R.drawable.ic_save_to_device,
                onClick = onExportAppsRequest
            )
        }
    }
}