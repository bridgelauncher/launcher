package com.tored.bridgelauncher.ui.dirpicker

import android.os.Environment
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.R
import com.tored.bridgelauncher.ui.theme.BridgeLauncherThemeStateless
import com.tored.bridgelauncher.ui.theme.scrim

@Composable
fun DirPickerDialogStateless(
    uiState: DirPickerUIState?,
    onFilterOrCreateDirTextChange: (String) -> Unit,
    onNavigateRequest: (Directory) -> Unit,
    onGrantPermissionRequest: () -> Unit,
    onCancelRequest: () -> Unit,
    onConfirmRequest: (Directory) -> Unit,
    onCreateDirRequest: () -> Unit,
    modifier: Modifier = Modifier,
)
{
    if (uiState != null)
    {
        BackHandler { onCancelRequest() }

        Surface(
            modifier = modifier
                .clickable(onClick = onCancelRequest, interactionSource = remember { MutableInteractionSource() }, indication = null),
            color = MaterialTheme.colors.scrim,
        )
        {
            Surface(
                modifier = Modifier
                    .systemBarsPadding()
                    .imePadding()
                    .padding(16.dp)
                    .fillMaxSize()
                    .clickable(onClick = {}, interactionSource = remember { MutableInteractionSource() }, indication = null),
                color = MaterialTheme.colors.background,
                shape = MaterialTheme.shapes.large,
                elevation = 8.dp,
            )
            {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                )
                {
                    if (uiState is DirPickerUIState.NoPermission)
                    {
                        DirPickerPermissionPrompt(
                            modifier = Modifier
                                .weight(1f),
                            onRequestGrantPermission = onGrantPermissionRequest,
                        )
                    }
                    else if (uiState is DirPickerUIState.HasPermission)
                    {
                        DirPickerHeader(
                            currentDir = uiState.currentDir,
                            upDir = uiState.upDir,
                            onNavigateRequest = onNavigateRequest,
                        )
                        Divider()
                        DirPickerCurrentDirContent(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            uiState = uiState,
                            onNavigateRequest = onNavigateRequest,
                        )
                    }


                    if (uiState is DirPickerUIState.HasPermission)
                    {
                        Divider()

                        if (uiState.exportState is DirPickerExportState.Exporting)
                        {
                            DirPickerProgressBar(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                exportState = uiState.exportState,
                            )
                        }
                        else
                        {
                            DirPickerFilterOrCreateDirBar(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                text = uiState.filterOrCreateDirText,
                                onTextChange = onFilterOrCreateDirTextChange,
                                onCreateDirectoryClick = onCreateDirRequest,
                            )
                        }
                    }

                    Divider()

                    val exportMode = uiState is DirPickerUIState.HasPermission && uiState.exportState != null
                    val exportInProgress = uiState is DirPickerUIState.HasPermission && uiState.exportState is DirPickerExportState.Exporting

                    DirPickerFooter(
                        modifier = Modifier
                            .fillMaxWidth(),
                        confirmText = if (exportMode)
                            "Export here"
                        else
                            "Use this directory",
                        confirmIcon = if (exportMode) R.drawable.ic_save_to_device else R.drawable.ic_check,
                        cancelText = if (exportInProgress) "Cancel export" else "Cancel",
                        isConfirmDisabled = uiState is DirPickerUIState.NoPermission || exportInProgress,
                        onCancelRequest = onCancelRequest,
                        onConfirmRequest = {
                            if (uiState is DirPickerUIState.HasPermission)
                                onConfirmRequest(uiState.currentDir)
                        }
                    )
                }
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
fun DirPickerDialogPreview()
{
    BridgeLauncherThemeStateless(useDarkTheme = false)
    {
        var currentDir by remember { mutableStateOf(Environment.getExternalStorageDirectory()) }
        var searchOrCreateDirText by remember { mutableStateOf("") }

        DirPickerDialogStateless(
            modifier = Modifier
                .fillMaxSize(),
            uiState = DirPickerUIState.HasPermission(
                currentDir = currentDir,
                filterOrCreateDirText = searchOrCreateDirText,
//                exportState = null,
                exportState = DirPickerExportState.Exporting(24, 112),
//                exportState = DirPickerExportState.NotExporting,
            ),
            onFilterOrCreateDirTextChange = { },
            onNavigateRequest = {
                currentDir = it
            },
            onGrantPermissionRequest = { },
            onCancelRequest = { },
            onConfirmRequest = { },
            onCreateDirRequest = { },
        )
    }
}
