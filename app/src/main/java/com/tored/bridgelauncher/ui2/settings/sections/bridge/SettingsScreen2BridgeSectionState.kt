package com.tored.bridgelauncher.ui2.settings.sections.bridge

import com.tored.bridgelauncher.services.settings2.BridgeThemeOptions

data class SettingsScreen2BridgeSectionState(
    val theme: BridgeThemeOptions,
    val showBridgeButton: Boolean,
    val showLaunchAppsWhenBridgeButtonCollapsed: Boolean,
    val isQSTileAdded: Boolean,
    val isQSTilePromptSupported: Boolean,
)