package com.tored.bridgelauncher.ui2.settings.sections.overlays

import com.tored.bridgelauncher.services.settings2.SystemBarAppearanceOptions

data class SettingsScreen2OverlaysSectionState(
    val statusBarAppearance: SystemBarAppearanceOptions,
    val navigationBarAppearance: SystemBarAppearanceOptions,
    val drawWebViewOverscrollEffects: Boolean,
)