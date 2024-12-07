package com.tored.bridgelauncher.ui2.settings.sections.overlays

import androidx.compose.runtime.Composable
import com.tored.bridgelauncher.services.settings2.SystemBarAppearanceOptions
import com.tored.bridgelauncher.ui2.shared.OptionsRow

@Composable
fun SystemBarAppearanceOptionsField(label: String, selectedOption: SystemBarAppearanceOptions, onChange: (SystemBarAppearanceOptions) -> Unit)
{
    OptionsRow(
        label = label,
        options = mapOf(
            SystemBarAppearanceOptions.Hide to "Hide",
            SystemBarAppearanceOptions.LightIcons to "Light icons",
            SystemBarAppearanceOptions.DarkIcons to "Dark icons",
        ),
        selectedOption = selectedOption,
        onChange = onChange
    )
}