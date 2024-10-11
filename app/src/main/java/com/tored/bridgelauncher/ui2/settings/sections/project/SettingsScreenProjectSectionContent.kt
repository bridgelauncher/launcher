package com.tored.bridgelauncher.ui2.settings.sections.project

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.ui.theme.BridgeLauncherThemeStateless

@Composable
fun SettingsScreenProjectSectionContent(
    state: SettingsScreenProjectSectionState,
    modifier: Modifier = Modifier
)
{
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    )
    {
        CurrentProjectCard(
            projectInfo = state.projectInfo,
            hasStoragePerms = state.hasStoragePerms,
            onChangeClick = { TODO() },
            onGrantPermissionRequest = { TODO() },
        )

        AllowProjectsToTurnScreenOffCheckbox(
            allowProjectsTurnScreenOff = state.allowProjectsToTurnScreenOff,
            hasNecessaryPermissions = false,
            screenLockingMethod = state.screenLockingMethod,
            onAllowProjectsTurnScreenOffChange = { TODO() },
            onGrantPermissionRequest = { TODO() },
        )
    }
}


// PREVIEWS

@Composable
fun SettingsScreenProjectSectionContentPreview(
    state: SettingsScreenProjectSectionState,
)
{
    BridgeLauncherThemeStateless {
        Surface()
        {
            SettingsScreenProjectSectionContent(
                modifier = Modifier.padding(8.dp),
                state = SettingsScreenProjectSectionState(
                    projectInfo = null,
                    hasStoragePerms = false,
                    allowProjectsToTurnScreenOff = false,
                    screenLockingMethod = ScreenLockingMethodOptions.DeviceAdmin,
                )
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun SettingsScreenProjectSectionContentPreview01()
{
    SettingsScreenProjectSectionContentPreview(
        state = SettingsScreenProjectSectionState(
            projectInfo = null,
            hasStoragePerms = false,
            allowProjectsToTurnScreenOff = false,
            screenLockingMethod = ScreenLockingMethodOptions.DeviceAdmin,
        )
    )
}
