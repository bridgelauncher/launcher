package com.tored.bridgelauncher.ui2.settings.sections.project

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.ui2.shared.PreviewWithSurfaceAndPadding

@Composable
fun SettingsScreen2ProjectSectionContent(
    state: SettingsScreen2ProjectSectionState,
    actions: SettingsScreen2ProjectSectionActions,
    modifier: Modifier = Modifier
)
{
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier,
    )
    {
        CurrentProjectCard(
            projectInfo = state.projectInfo,
            hasStoragePerms = state.hasStoragePerms,
            onChangeClick = actions.changeProject,
            onGrantPermissionRequest = actions.requestGrantStoragePerms,
        )

        AllowProjectsToTurnScreenOffCheckbox(
            allowProjectsTurnScreenOff = state.allowProjectsToTurnScreenOff,
            hasNecessaryPermissions = state.canBridgeTurnScreenOff,
            screenLockingMethod = state.screenLockingMethod,
            onAllowProjectsTurnScreenOffChange = actions.changeAllowProjectsToTurnScreenOff,
            onGrantPermissionRequest = actions.requestGrantStoragePerms,
        )
    }
}


// PREVIEWS

@Composable
fun SettingsScreenProjectSectionContentPreview(
    projectInfo: SettingsScreen2ProjectSectionStateProjectInfo? = null,
    hasStoragePerms: Boolean = false,
    allowProjectsToTurnScreenOff: Boolean = false,
    screenLockingMethod: ScreenLockingMethodOptions = ScreenLockingMethodOptions.AccessibilityService,
    canBridgeTurnScreenOff: Boolean = false,

    )
{
    PreviewWithSurfaceAndPadding {
        SettingsScreen2ProjectSectionContent(
            state = SettingsScreen2ProjectSectionState(
                projectInfo = projectInfo,
                hasStoragePerms = hasStoragePerms,
                allowProjectsToTurnScreenOff = allowProjectsToTurnScreenOff,
                screenLockingMethod = screenLockingMethod,
                canBridgeTurnScreenOff = canBridgeTurnScreenOff
            ),
            actions = SettingsScreen2ProjectSectionActions({}, {}, {}),
        )
    }
}

@Composable
@PreviewLightDark
private fun SettingsScreenProjectSectionContentPreview01()
{
    SettingsScreenProjectSectionContentPreview()
}
