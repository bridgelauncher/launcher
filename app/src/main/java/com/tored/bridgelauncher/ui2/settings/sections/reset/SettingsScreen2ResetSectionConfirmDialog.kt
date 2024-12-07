package com.tored.bridgelauncher.ui2.settings.sections.reset

import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.tored.bridgelauncher.ui2.shared.Btn
import com.tored.bridgelauncher.ui2.theme.BridgeLauncherThemeStateless

@Composable
fun SettingsScreen2ResetSectionConfirmDialog(
    isResetDisabled: Boolean,
    onDismissRequest: () -> Unit,
    onResetRequest: () -> Unit,
)
{
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = { Text("Confirm reset") },
        text = { Text("Are you sure you want to reset to default settings? This action cannot be undone.") },
        confirmButton = {
            Btn(
                text = "Reset",
                contentColor = MaterialTheme.colors.error,
                disabled = isResetDisabled,
                onClick = { onResetRequest() },
            )
        },
        dismissButton = {
            Btn(
                text = "Cancel",
                contentColor = MaterialTheme.colors.onSurface,
                onClick = { onDismissRequest() }
            )
        }
    )
}


// PREVIEWS

@Composable
fun SettingsScreen2ResetSectionConfirmDialogPreview(
    isResetDisabled: Boolean = false,
)
{
    BridgeLauncherThemeStateless {
        SettingsScreen2ResetSectionConfirmDialog(
            isResetDisabled = isResetDisabled,
            onDismissRequest = {},
            onResetRequest = {}
        )
    }
}

@Composable
@PreviewLightDark
private fun SettingsScreen2ResetSectionConfirmDialogPreview_ResetEnabled()
{
    SettingsScreen2ResetSectionConfirmDialogPreview()
}

@Composable
@PreviewLightDark
private fun SettingsScreen2ResetSectionConfirmDialogPreview_ResetDisabled()
{
    SettingsScreen2ResetSectionConfirmDialogPreview(isResetDisabled = true)
}