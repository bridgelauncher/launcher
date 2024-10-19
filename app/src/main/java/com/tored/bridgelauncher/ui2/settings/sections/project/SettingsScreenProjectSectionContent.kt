package com.tored.bridgelauncher.ui2.settings.sections.project

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.ui2.shared.PreviewWithSurfaceAndPadding
import com.tored.bridgelauncher.utils.CurrentAndroidVersion
import com.tored.bridgelauncher.utils.tryStartAndroidAccessibilitySettingsActivity
import com.tored.bridgelauncher.utils.tryStartAndroidAddDeviceAdminActivity

private const val TAG = "SettingsScreen2ProjectSectionContent"

@Composable
fun SettingsScreen2ProjectSectionContent(
    state: SettingsScreen2ProjectSectionState,
    actions: SettingsScreen2ProjectSectionActions,
    requestStoragePermission: () -> Unit,
    modifier: Modifier = Modifier,
)
{
    val permsLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) { areGranted ->
        actions.onStoragePermsStateChanged(areGranted)
    }

    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier,
    )
    {
        CurrentProjectCard(
            projectInfo = state.projectInfo,
            hasStoragePerms = state.hasStoragePerms,
            onChangeClick = actions.changeProject,
            onGrantPermissionRequest = { requestStoragePermission() },
        )

        AllowProjectsToTurnScreenOffCheckbox(
            allowProjectsTurnScreenOff = state.allowProjectsToTurnScreenOff,
            hasNecessaryPermissions = state.canBridgeTurnScreenOff,
            screenLockingMethod = state.screenLockingMethod,
            onAllowProjectsTurnScreenOffChange = actions.changeAllowProjectsToTurnScreenOff,
            onGrantPermissionRequest = {
                if (CurrentAndroidVersion.supportsAccessiblityServiceScreenLock())
                {
                    Log.d(TAG, "Accessiblity Service not enabled. Redirecting user to settings.")
                    context.tryStartAndroidAccessibilitySettingsActivity()
                }
                else
                {
                    Log.d(TAG, "Device admin is not enabled. Redirecting user to settings.")
                    context.tryStartAndroidAddDeviceAdminActivity()
                }
            },
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
            actions = SettingsScreen2ProjectSectionActions.empty(),
            requestStoragePermission = {}
        )
    }
}

@Composable
@PreviewLightDark
private fun SettingsScreenProjectSectionContentPreview01()
{
    SettingsScreenProjectSectionContentPreview()
}
