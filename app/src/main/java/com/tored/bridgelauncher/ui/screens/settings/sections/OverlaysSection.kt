package com.tored.bridgelauncher.ui.screens.settings.sections

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tored.bridgelauncher.R
import com.tored.bridgelauncher.settings.SettingsState
import com.tored.bridgelauncher.settings.SettingsVM
import com.tored.bridgelauncher.ui.screens.settings.SettingsCheckboxFieldFor
import com.tored.bridgelauncher.ui.screens.settings.SettingsSection
import com.tored.bridgelauncher.ui.screens.settings.SettingsSystemBarOptionsFieldFor

@Composable
fun SettingsOverlaysSection(vm: SettingsVM = viewModel())
{
    SettingsSection(label = "Overlays", iconResId = R.drawable.ic_overlays)
    {
        SettingsSystemBarOptionsFieldFor(SettingsState::statusBarAppearance)
        SettingsSystemBarOptionsFieldFor(SettingsState::navigationBarAppearance)
        SettingsCheckboxFieldFor(SettingsState::drawWebViewOverscrollEffects)
    }
}