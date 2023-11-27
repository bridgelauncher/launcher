package com.tored.bridgelauncher.ui.screens.settings

import androidx.compose.runtime.Composable
import com.tored.bridgelauncher.settings.SystemBarAppearanceOptions
import com.tored.bridgelauncher.ui.shared.OptionsRow

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