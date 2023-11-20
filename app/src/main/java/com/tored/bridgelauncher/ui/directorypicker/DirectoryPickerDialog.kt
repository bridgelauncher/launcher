package com.tored.bridgelauncher.ui.directorypicker

import android.os.Environment
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.tored.bridgelauncher.ui.theme.BridgeLauncherThemeStateless
import com.tored.bridgelauncher.ui.theme.scrim

@Composable
fun DirectoryPickerDialogStateless(
    uiState: DirectoryPickerUIState?,
    onNavigateRequest: (Directory) -> Unit,
    onGrantPermissionRequest: () -> Unit,
    onCancelRequest: () -> Unit,
    onConfirmRequest: (Directory) -> Unit,
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
@Preview(showBackground = true)
fun DirectoryPickerDialogPreview()
{
    BridgeLauncherThemeStateless(useDarkTheme = false)
    {
        var currentDir by remember { mutableStateOf(Environment.getExternalStorageDirectory()) }

        DirectoryPickerDialogStateless(
            modifier = Modifier
                .fillMaxSize(),
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
