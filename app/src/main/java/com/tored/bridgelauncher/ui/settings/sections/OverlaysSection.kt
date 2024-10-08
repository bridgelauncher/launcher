package com.tored.bridgelauncher.ui.settings.sections

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tored.bridgelauncher.R
import com.tored.bridgelauncher.services.settings.SettingsState
import com.tored.bridgelauncher.services.settings.SettingsVM
import com.tored.bridgelauncher.ui.settings.SettingsCheckboxFieldFor
import com.tored.bridgelauncher.ui.settings.SettingsSection
import com.tored.bridgelauncher.ui.settings.SettingsSystemBarOptionsFieldFor

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