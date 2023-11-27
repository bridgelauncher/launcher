package com.tored.bridgelauncher.ui.screens.settings.sections

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
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
import com.tored.bridgelauncher.utils.writeBool

@Composable
fun SettingsProjectSection(
    isExtStorageManager: Boolean,
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
            isExtStorageManager = isExtStorageManager,
            onGrantPermissionRequest = onGrantPermissionRequest
        )

        val prop = SettingsState::allowProjectsToTurnScreenOff
        CheckboxField(
            label = displayNameFor(prop),
            description = if (uiState.isDeviceAdminEnabled) "Bridge has device admin permissions." else "Tap to grant Bridge device admin permissions.",
            isChecked = prop.getValue(uiState, prop),
            onCheckedChange = { isChecked ->
                if (uiState.isDeviceAdminEnabled)
                {
                    vm.edit {
                        writeBool(prop, isChecked)
                    }
                }
                else
                {
                    context.startActivity(
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
            }
        )

//                    Btn(text = "Lock the screen")
//                    {
//                        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
//                        if (dpm.isAdminActive(adminReceiverComponentName))
//                        {
//                            dpm.lockNow()
//                        }
//                        else
//                        {
//                            Toast
//                                .makeText(
//                                    context,
//                                    "Bridge is not a device admin. Visit Bridge settings to resolve this issue.",
//                                    Toast.LENGTH_LONG
//                                )
//                                .show()
//                        }
//                    }
    }
}
