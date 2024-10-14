package com.tored.bridgelauncher.ui2.settings.sections.project

data class  SettingsScreen2ProjectSectionStateProjectInfo(
    val name: String,
)

data class SettingsScreen2ProjectSectionState(
    val projectInfo: SettingsScreen2ProjectSectionStateProjectInfo?,
    val hasStoragePerms: Boolean,
    val allowProjectsToTurnScreenOff: Boolean,
    val screenLockingMethod: ScreenLockingMethodOptions,
    val canBridgeTurnScreenOff: Boolean,
)
