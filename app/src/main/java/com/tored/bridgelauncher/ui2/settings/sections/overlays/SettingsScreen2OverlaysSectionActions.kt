package com.tored.bridgelauncher.ui2.settings.sections.overlays

import com.tored.bridgelauncher.services.settings.SystemBarAppearanceOptions

data class SettingsScreen2OverlaysSectionActions(
    val changeStatusBarAppearance: (newValue: SystemBarAppearanceOptions) -> Unit,
    val changeNavigationBarAppearance: (newValue: SystemBarAppearanceOptions) -> Unit,
    val changeDrawWebViewOverscrollEffects: (newValue: Boolean) -> Unit,
)
{
    companion object
    {
        fun empty() = SettingsScreen2OverlaysSectionActions({}, {}, {})
    }
}