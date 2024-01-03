package com.tored.bridgelauncher.ui.screens.settings.sections

import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tored.bridgelauncher.R
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
            description = if (uiState.isAccessibilityServiceEnabled)
                "Bridge Accessibility Service is enabled."
            else
                "Tap to enable the Bridge Accessibility Service.",
            isChecked = prop.getValue(uiState, prop),
            onCheckedChange = { newChecked ->
                if (uiState.isAccessibilityServiceEnabled)
                {
                    Log.d(TAG, "Accessiblity Service already enabled. Setting checked to $newChecked")
                    vm.edit {
                        writeBool(prop, newChecked)
                    }
                }
                else
                {
                    Log.d(TAG, "Accessiblity Service not enabled. Redirecting user to settings.")
                    context.tryStartActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                }
            }
        )
    }
}
