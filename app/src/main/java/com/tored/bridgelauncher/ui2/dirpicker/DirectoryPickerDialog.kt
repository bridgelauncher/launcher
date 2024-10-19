package com.tored.bridgelauncher.ui2.dirpicker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.tored.bridgelauncher.R
import com.tored.bridgelauncher.ui2.DirectoryPicker.composables.DirectoryPickerStartupSubfileListItem
import com.tored.bridgelauncher.ui2.DirectoryPicker.composables.DirectoryPickerSubdirListItem
import com.tored.bridgelauncher.ui2.DirectoryPicker.composables.DirectoryPickerSubfileListItem
import com.tored.bridgelauncher.ui2.dirpicker.composables.DirectoryPickerFilterCreateDirectoryBar
import com.tored.bridgelauncher.ui2.dirpicker.composables.DirectoryPickerFooter
import com.tored.bridgelauncher.ui2.dirpicker.composables.DirectoryPickerHeader
import com.tored.bridgelauncher.ui2.dirpicker.composables.DirectoryPickerStoragePermissionPrompt
import com.tored.bridgelauncher.ui2.shared.PreviewWithSurfaceAndPadding

@Composable
fun DirectoryPickerDialog(
    state: DirectoryPickerState,
    actions: DirectoryPickerActions,
    requestStoragePermission: () -> Unit,
)
{
    Dialog(
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
        onDismissRequest = { actions.dismiss() },
    )
    {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .padding(16.dp)
        )
        {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = {}, interactionSource = remember { MutableInteractionSource() }, indication = null),
                color = MaterialTheme.colors.background,
                shape = MaterialTheme.shapes.large,
                elevation = 8.dp,
            )
            {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                )
                {
                    when (state)
                    {
                        is DirectoryPickerState.NoStoragePermission ->
                        {
                            DirectoryPickerStoragePermissionPrompt(
                                state.supportsScopedStorage,
                                { requestStoragePermission() },
                                modifier = Modifier.weight(1f),
                            )
                        }

                        is DirectoryPickerState.HasStoragePermission ->
                        {
                            DirectoryPickerHeader(
                                currentDir = state.currentDirectory,
                                upDir = state.upDirectory,
                                requestUp = {
                                    if (state.upDirectory != null)
                                        actions.navigateToDirectory(state.upDirectory)
                                }
                            )

                            Divider()

                            LazyColumn(
                                modifier = Modifier.weight(1f),
                            )
                            {
                                items(state.directories)
                                {
                                    DirectoryPickerSubdirListItem(
                                        dir = it,
                                        onClick = { actions.navigateToDirectory(it) }
                                    )
                                }

                                items(state.startupFiles)
                                {
                                    DirectoryPickerStartupSubfileListItem(
                                        file = it,
                                        onClick = { actions.selectCurrentDirectory() }
                                    )
                                }

                                items(state.regularFiles)
                                {
                                    DirectoryPickerSubfileListItem(
                                        file = it
                                    )
                                }
                            }

                            Divider()

                            DirectoryPickerFilterCreateDirectoryBar(
                                text = state.filterOrCreateDirectoryText,
                                onTextChange = { actions.requestFilterOrCreateDirectoryTextChange(it) },
                                onCreateDirectoryClick = { actions.createSubdirectory() },
                            )
                        }
                    }

                    Divider()

                    DirectoryPickerFooter(
                        confirmText = when (state.mode)
                        {
                            DirectoryPickerMode.LoadProject -> "Load project"
                            DirectoryPickerMode.MockExport -> "Export here"
                        },
                        confirmIconResId = when (state.mode)
                        {
                            DirectoryPickerMode.LoadProject -> R.drawable.ic_check
                            DirectoryPickerMode.MockExport -> R.drawable.ic_save_to_device
                        },
                        isConfirmDisabled = state is DirectoryPickerState.NoStoragePermission,
                        cancelText = "Cancel",
                        onConfirmRequest = { actions.selectCurrentDirectory() },
                        onCancelRequest = { actions.dismiss() },
                    )
                }
            }
        }
    }
}


// PREVIEWS

@Composable
fun DirectoryPickerDialogPreview_NoStoragePermission(
    mode: DirectoryPickerMode = DirectoryPickerMode.LoadProject,
    supportsScopedStorage: Boolean = false,
)
{
    PreviewWithSurfaceAndPadding {
        DirectoryPickerDialog(
            state = DirectoryPickerState.NoStoragePermission(
                mode = mode,
                supportsScopedStorage = supportsScopedStorage
            ),
            actions = DirectoryPickerActions.empty(),
            requestStoragePermission = {}
        )
    }
}

@Composable
@PreviewLightDark
fun DirectoryPickerDialogPreview_NoStoragePermission_NoScopedStorage()
{
    DirectoryPickerDialogPreview_NoStoragePermission(mode = DirectoryPickerMode.MockExport)
}

@Composable
@PreviewLightDark
fun DirectoryPickerDialogPreview_NoStoragePermission_ScopedStorage()
{
    DirectoryPickerDialogPreview_NoStoragePermission(supportsScopedStorage = true)
}


private val DummyDirectories = listOf(
    DirectoryPickerDummyDirectory("dogs", "/up/dogs"),
    DirectoryPickerDummyDirectory("cats", "/up/cats", canRead = false),
)

private val DummyStartupFiles = listOf(
    DirectoryPickerDummyFile("index.html", "/up/index.html"),
)

private val DummyRegularFiles = listOf(
    DirectoryPickerDummyFile("main.js", "/up/main.js"),
    DirectoryPickerDummyFile("shared.css", "/up/shared.css"),
    DirectoryPickerDummyFile("shared.min.css", "/up/shared.min.css"),
)

@Composable
fun DirectoryPickerDialogPreview_HasStoragePermission(
    currentDirectory: DirectoryPickerDirectory,
    mode: DirectoryPickerMode = DirectoryPickerMode.LoadProject,
    upDirectory: DirectoryPickerDirectory? = null,
    filterOrCreateDirecoryText: String = "",
)
{
    PreviewWithSurfaceAndPadding {
        DirectoryPickerDialog(
            state = DirectoryPickerState.HasStoragePermission(
                mode = mode,
                currentDirectory = currentDirectory,
                upDirectory = upDirectory,
                directories = DummyDirectories,
                startupFiles = DummyStartupFiles,
                regularFiles = DummyRegularFiles,
                filterOrCreateDirectoryText = filterOrCreateDirecoryText,
            ),
            actions = DirectoryPickerActions.empty(),
            requestStoragePermission = {}
        )
    }
}

@Composable
@PreviewLightDark
fun DirectoryPickerDialogPreview_HasStoragePermission_01()
{
    DirectoryPickerDialogPreview_HasStoragePermission(
        currentDirectory = DirectoryPickerDummyDirectory("up", "/up"),
        upDirectory = DirectoryPickerDummyDirectory("/", "/"),
    )
}

@Composable
@PreviewLightDark
fun DirectoryPickerDialogPreview_HasStoragePermission_02()
{
    DirectoryPickerDialogPreview_HasStoragePermission(
        currentDirectory = DirectoryPickerDummyDirectory("up", "/up"),
    )
}