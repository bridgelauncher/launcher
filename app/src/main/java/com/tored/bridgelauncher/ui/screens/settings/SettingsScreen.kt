package com.tored.bridgelauncher.ui.screens.settings

import android.os.Environment
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tored.bridgelauncher.settings.SettingsState
import com.tored.bridgelauncher.settings.SettingsVM
import com.tored.bridgelauncher.settings.SystemBarAppearanceOptions
import com.tored.bridgelauncher.ui.directorypicker.DirectoryPickerDialogStateless
import com.tored.bridgelauncher.ui.directorypicker.DirectoryPickerUIState
import com.tored.bridgelauncher.ui.screens.settings.sections.SettingsAboutSection
import com.tored.bridgelauncher.ui.screens.settings.sections.SettingsBridgeSection
import com.tored.bridgelauncher.ui.screens.settings.sections.SettingsDevelopmentSection
import com.tored.bridgelauncher.ui.screens.settings.sections.SettingsOverlaysSection
import com.tored.bridgelauncher.ui.screens.settings.sections.SettingsProjectSection
import com.tored.bridgelauncher.ui.screens.settings.sections.SettingsSystemWallpaperSection
import com.tored.bridgelauncher.ui.theme.BridgeLauncherTheme
import com.tored.bridgelauncher.utils.writeDir

@Composable
fun SettingsScreen(
    isExtStorageManager: Boolean,
    onGrantPermissionRequest: () -> Unit,
    vm: SettingsVM = viewModel(),
)
{
    val uiState by vm.settingsUIState.collectAsStateWithLifecycle()
    LaunchedEffect(vm) { vm.request() }

    var dirPickerCurrentDir by remember { mutableStateOf(uiState.currentProjDir ?: Environment.getExternalStorageDirectory()) }
    var dirPickerIsOpen by remember { mutableStateOf(false) }

    val context = LocalContext.current

    SettingsScreenSetSystemBars()


    Box(
        modifier = Modifier.fillMaxSize()
    )
    {
        Surface(
            color = MaterialTheme.colors.background
        )
        {
            Column(
                modifier = Modifier
                    .systemBarsPadding(),
            )
            {

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(0.dp, 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                )
                {
                    SettingsProjectSection(
                        isExtStorageManager = isExtStorageManager,
                        onGrantPermissionRequest = onGrantPermissionRequest,
                        onChangeProjectDirRequest = {
                            dirPickerCurrentDir = uiState.currentProjDir
                            dirPickerIsOpen = true
                        }
                    )

                    Divider()

                    SettingsSystemWallpaperSection()

                    Divider()

                    SettingsOverlaysSection()

                    Divider()

                    SettingsBridgeSection()

                    Divider()

                    SettingsDevelopmentSection()

                    Divider()

                    SettingsAboutSection()
                }

                SettingsBotBar()
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(with(LocalDensity.current) {
                    WindowInsets.navigationBars
                        .getBottom(this)
                        .toDp()
                }),
            color = MaterialTheme.colors.surface,
        ) { }

        DirectoryPickerDialogStateless(
            modifier = Modifier
                .fillMaxSize(),
            uiState = if (dirPickerIsOpen)
            {
                if (isExtStorageManager)
                    DirectoryPickerUIState.HasPermission(dirPickerCurrentDir)
                else
                    DirectoryPickerUIState.NoPermission
            }
            else
            {
                null
            },
            onGrantPermissionRequest = onGrantPermissionRequest,
            onNavigateRequest = { dir ->
                dirPickerCurrentDir = dir
            },
            onCancelRequest = {
                dirPickerIsOpen = false
            },
            onConfirmRequest = { dir ->
                vm.edit {
                    writeDir(SettingsState::currentProjDir, dir)
                }
                dirPickerIsOpen = false
            }
        )
    }
}


@Composable
@Preview(showBackground = true)
fun SettingsPreview()
{
    BridgeLauncherTheme {
        SystemBarAppearanceOptionsField(
            "Status bar",
            SystemBarAppearanceOptions.Hide,
            onChange = { }
        )
    }
}