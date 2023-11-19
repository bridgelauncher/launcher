package com.tored.bridgelauncher.ui.directorypicker

import android.os.Environment
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.tored.bridgelauncher.ui.theme.BridgeLauncherThemeStateless

@Composable
fun DirectoryPickerDialogStateless(
    isOpen: Boolean,
    uiState: DirectoryPickerUIState,
    onNavigateRequest: (Directory) -> Unit,
    onGrantPermissionRequest: () -> Unit,
    onCancelRequest: () -> Unit,
    onConfirmRequest: (Directory) -> Unit,
    modifier: Modifier = Modifier,
)
{
    if (isOpen)
    {
        Dialog(
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            ),
            onDismissRequest = onCancelRequest,
        )
        {
            Surface(
                modifier = modifier,
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
                    if (uiState is DirectoryPickerUIState.NoPermission)
                    {
                        DirectoryPickerPermissionPrompt(
                            modifier = Modifier
                                .weight(1f),
                            onRequestGrantPermission = onGrantPermissionRequest,
                        )
                    }
                    else if (uiState is DirectoryPickerUIState.HasPermission)
                    {
                        DirectoryPickerHeader(
                            currentDir = uiState.currentDir,
                            upDir = uiState.upDir,
                            onNavigateRequest = onNavigateRequest,
                        )
                        Divider()
                        DirectoryPickerCurrentDirContent(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            uiState = uiState,
                            onNavigateRequest = onNavigateRequest,
                        )
                    }

                    Divider()
                    DirectoryPickerFooter(
                        modifier = Modifier
                            .fillMaxWidth(),
                        isConfirmDisabled = uiState is DirectoryPickerUIState.NoPermission,
                        onCancelRequest = onCancelRequest,
                        onConfirmRequest = {
                            if (uiState is DirectoryPickerUIState.HasPermission)
                                onConfirmRequest(uiState.currentDir)
                        }
                    )
                }
            }
        }
    }
}


@Composable
@Preview
fun DirectoryPickerDialogPreview()
{
    BridgeLauncherThemeStateless(useDarkTheme = false)
    {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.surface
        ) {}

        var currentDir by remember { mutableStateOf(Environment.getExternalStorageDirectory()) }

        DirectoryPickerDialogStateless(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            isOpen = true,
            uiState = DirectoryPickerUIState.HasPermission(
                currentDir = currentDir,
            ),
            onNavigateRequest = {
                currentDir = it
            },
            onGrantPermissionRequest = {

            },
            onCancelRequest = {

            },
            onConfirmRequest = {

            }
        )
    }
}
