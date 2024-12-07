package com.tored.bridgelauncher.ui2.settings.sections.reset

data class SettingsScreen2ResetSectionActions(
    val onResetRequest: () -> Unit,
)
{
    companion object {
        fun empty() = SettingsScreen2ResetSectionActions({})
    }
}