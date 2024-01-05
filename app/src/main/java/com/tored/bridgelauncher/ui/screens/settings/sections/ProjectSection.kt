package com.tored.bridgelauncher.ui.screens.settings.sections

import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tored.bridgelauncher.R
import com.tored.bridgelauncher.services.BridgeLauncherDeviceAdminReceiver
import com.tored.bridgelauncher.settings.SettingsState
import com.tored.bridgelauncher.settings.SettingsVM
import com.tored.bridgelauncher.ui.screens.settings.CurrentProjectCard
import com.tored.bridgelauncher.ui.screens.settings.SettingsSection
import com.tored.bridgelauncher.ui.shared.CheckboxField
import com.tored.bridgelauncher.utils.displayNameFor
import com.tored.bridgelauncher.utils.tryStartActivity
import com.tored.bridgelauncher.utils.writeBool

private const val TAG = "SettProjSect"

@Composable
fun SettingsProjectSection(
    hasStoragePerms: Boolean,
    onGrantPermissionRequest: () -> Unit,
    onChangeProjectDirRequest: () -> Unit,
    vm: SettingsVM = viewModel(),
)
{
    val uiState by vm.settingsUIState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val adminReceiverComponentName = ComponentName(context, BridgeLauncherDeviceAdminReceiver::class.java)

    SettingsSection(label = "Project", iconResId = R.drawable.ic_folder_open)
    {
        CurrentProjectCard(
            uiState.currentProjDir,
            onChangeClick = onChangeProjectDirRequest,
            hasStoragePerms = hasStoragePerms,
            onGrantPermissionRequest = onGrantPermissionRequest
        )

        val prop = SettingsState::allowProjectsToTurnScreenOff
        CheckboxField(
            label = displayNameFor(prop),
            description = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P)
            {
                if (uiState.isDeviceAdminEnabled)
                    "Bridge is a device admin."
                else
                    "Tap to grant Bridge device admin permissions."
            }
            else
            {
                if (uiState.isAccessibilityServiceEnabled)
                    "Bridge Accessibility Service is enabled."
                else
                    "Tap to enable the Bridge Accessibility Service."
            },
            isChecked = prop.getValue(uiState, prop),
            onCheckedChange = { newChecked ->

                if (
                    (Build.VERSION.SDK_INT < Build.VERSION_CODES.P && uiState.isDeviceAdminEnabled)
                    || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && uiState.isAccessibilityServiceEnabled)
                )
                {
                    Log.d(TAG, "Can lock screen already. Setting checked to $newChecked")
                    vm.edit {
                        writeBool(prop, newChecked)
                    }
                }
                else @SuppressLint("ObsoleteSdkInt")
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P)
                {
                    Log.d(TAG, "Device admin is not enabled. Redirecting user to settings.")
                    context.tryStartActivity(
                        Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                            putExtra(
                                DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                                adminReceiverComponentName
                            )
                            putExtra(
                                DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                                "Bridge Launcher needs this permission so projects can request the screen to be locked."
                            )
                        }
                    )
                }
                else
                {
                    assert(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                    Log.d(TAG, "Accessiblity Service not enabled. Redirecting user to settings.")
                    context.tryStartActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                }
            }
        )
    }
}
