package com.tored.bridgelauncher.ui2.settings.sections.project

data class  SettingsScreenProjectSectionStateProjectInfo(
    val name: String,
)

data class SettingsScreen2ProjectSectionState(
    val projectInfo: SettingsScreenProjectSectionStateProjectInfo?,
    val hasStoragePerms: Boolean,
    val allowProjectsToTurnScreenOff: Boolean,
    val screenLockingMethod: ScreenLockingMethodOptions,
)
