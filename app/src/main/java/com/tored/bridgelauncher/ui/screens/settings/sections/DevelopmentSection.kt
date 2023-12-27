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
//        ActionCard(
//            title = "Bridge Developer Hub",
//            description = "Documentation and tools to help you develop Bridge Launcher projects."
//        )
//        {
//            val context = LocalContext.current
//            Btn(text = "Open in browser", suffixIcon = R.drawable.ic_open_in_new, onClick = {
//                Toast.makeText(context, "The Developer Hub does not exist yet. Sorry!", Toast.LENGTH_SHORT).show()
//            })
//        }

        ActionCard(
            title = "Export installed apps",
            descriptionParagraphs = listOf(
                "You will be prompted to select a directory. "
                        + "The export will contain a list of apps installed on this device and their icons.",

                "These files help mock the Bridge JS to Android API for development purposes.\n"
                        + "More information available on the project home page.",
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