package com.tored.bridgelauncher.ui2.settings.sections.reset

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.tored.bridgelauncher.ui2.shared.Btn
import com.tored.bridgelauncher.ui2.shared.PreviewWithSurfaceAndPadding

@Composable
fun SettingsScreen2ResetSectionContent(
    state: SettingsScreen2ResetSectionState,
    actions: SettingsScreen2ResetSectionActions,
    modifier: Modifier = Modifier,
)
{
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {

        var isConfirmDialogShown by remember { mutableStateOf(false) }

        Btn(
            text = "Reset to default settings",
            contentColor = MaterialTheme.colors.error,
            disabled = state.isResetInProgress,
            onClick = {
                isConfirmDialogShown = true
            }
        )

        if (isConfirmDialogShown)
        {
            SettingsScreen2ResetSectionConfirmDialog(
                isResetDisabled = state.isResetInProgress,
                onDismissRequest = { isConfirmDialogShown = false },
                onResetRequest = {
                    actions.onResetRequest()
                    isConfirmDialogShown = false
                }
            )
        }
    }
}


// PREVIEWS

@Composable
fun SettingsScreen2ResetSectionPreview(
    isResetInProgress: Boolean = false,
)
{
    PreviewWithSurfaceAndPadding {
        SettingsScreen2ResetSectionContent(
            state = SettingsScreen2ResetSectionState(
                isResetInProgress = isResetInProgress
            ),
            actions = SettingsScreen2ResetSectionActions.empty(),
        )
    }
}

@Composable
@PreviewLightDark
fun SettingsScreen2ResetSectionPreview_Normal()
{
    SettingsScreen2ResetSectionPreview()
}

@Composable
@PreviewLightDark
fun SettingsScreen2ResetSectionPreview_ResetInProgress()
{
    SettingsScreen2ResetSectionPreview(
        isResetInProgress = true
    )
}