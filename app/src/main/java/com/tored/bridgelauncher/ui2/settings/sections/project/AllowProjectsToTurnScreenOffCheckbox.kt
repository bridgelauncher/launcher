package com.tored.bridgelauncher.ui2.settings.sections.project

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tored.bridgelauncher.services.settings.SettingsState
import com.tored.bridgelauncher.ui.shared.CheckboxField
import com.tored.bridgelauncher.utils.displayNameFor

enum class ScreenLockingMethodOptions
{
    DeviceAdmin,
    AccessibilityService,
}

@Composable
fun AllowProjectsToTurnScreenOffCheckbox(
    allowProjectsTurnScreenOff: Boolean,
    hasNecessaryPermissions: Boolean,
    screenLockingMethod: ScreenLockingMethodOptions,
    onAllowProjectsTurnScreenOffChange: (newChecked: Boolean) -> Unit,
    onGrantPermissionRequest: () -> Unit,
    modifier: Modifier = Modifier
)
{
    val prop = SettingsState::allowProjectsToTurnScreenOff

    val description = when (screenLockingMethod)
    {
        ScreenLockingMethodOptions.DeviceAdmin ->
        {
            if (hasNecessaryPermissions)
                "Bridge is a device admin."
            else
                "Tap to grant Bridge device admin permissions."
        }

        ScreenLockingMethodOptions.AccessibilityService ->
        {
            if (hasNecessaryPermissions)
                "Bridge Accessibility Service is enabled."
            else
                "Tap to enable the Bridge Accessibility Service."
        }

        else -> throw NotImplementedError("Invalid screen locking method $screenLockingMethod.")
    }


//    if (CurrentAndroidVersion.supportsAccessiblityServiceScreenLock())
//    {
//        if (uiState.isAccessibilityServiceEnabled)
//            "Bridge Accessibility Service is enabled."
//        else
//            "Tap to enable the Bridge Accessibility Service."
//    }
//    else
//    {
//        if (uiState.isDeviceAdminEnabled)
//            "Bridge is a device admin."
//        else
//            "Tap to grant Bridge device admin permissions."
//    }

    CheckboxField(
        label = displayNameFor(prop),
        description = description,
        isChecked = allowProjectsTurnScreenOff,
        onCheckedChange = { newChecked ->

            if (!hasNecessaryPermissions)
                onGrantPermissionRequest()
            else
                onAllowProjectsTurnScreenOffChange(newChecked)
        }
    )

//            if (
//                (!CurrentAndroidVersion.supportsAccessiblityServiceScreenLock() && uiState.isDeviceAdminEnabled)
//                || (CurrentAndroidVersion.supportsAccessiblityServiceScreenLock() && uiState.isAccessibilityServiceEnabled)
//            )
//            {
//                Log.d(TAG, "Can lock screen already. Setting checked to $newChecked")
//                vm.edit {
//                    writeBool(prop, newChecked)
//                }
//            }
//            else @SuppressLint("ObsoleteSdkInt")
//            if (CurrentAndroidVersion.supportsAccessiblityServiceScreenLock())
//            {
//                Log.d(TAG, "Accessiblity Service not enabled. Redirecting user to settings.")
//                context.tryStartActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
//            }
//            else
//            {
//                Log.d(TAG, "Device admin is not enabled. Redirecting user to settings.")
//                context.tryStartActivity(
//                    Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
//                        putExtra(
//                            DevicePolicyManager.EXTRA_DEVICE_ADMIN,
//                            adminReceiverComponentName
//                        )
//                        putExtra(
//                            DevicePolicyManager.EXTRA_ADD_EXPLANATION,
//                            "Bridge Launcher needs this permission so projects can request the screen to be locked."
//                        )
//                    }
//                )
//            }
}