package com.tored.bridgelauncher.ui2.settings.sections.systembars

import com.tored.bridgelauncher.services.settings.SystemBarAppearanceOptions

data class SettingsScreen2OverlaysSectionState(
    val statusBarAppearance: SystemBarAppearanceOptions,
    val navigationBarAppearance: SystemBarAppearanceOptions,
    val drawWebViewOverscrollEffects: Boolean,
)