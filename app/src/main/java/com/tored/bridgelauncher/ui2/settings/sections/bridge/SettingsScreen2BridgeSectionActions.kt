package com.tored.bridgelauncher.ui2.settings.sections.bridge

import com.tored.bridgelauncher.services.settings.ThemeOptions

data class SettingsScreen2BridgeSectionActions(
    val changeTheme: (newValue: ThemeOptions) -> Unit,
    val changeShowBridgeButton: (newValue: Boolean) -> Unit,
    val changeShowLaunchAppsWhenBridgeButtonCollapsed: (newValue: Boolean) -> Unit,
    val requestQSTilePrompt: () -> Unit,
)
{
    companion object
    {
        fun empty() = SettingsScreen2BridgeSectionActions({}, {}, {}, {})
    }
}
