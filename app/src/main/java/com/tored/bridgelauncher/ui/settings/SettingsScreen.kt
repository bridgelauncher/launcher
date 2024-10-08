package com.tored.bridgelauncher.ui.settings

import android.os.Environment
import android.widget.Toast
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tored.bridgelauncher.BridgeLauncherApplication
import com.tored.bridgelauncher.annotations.DontReset
import com.tored.bridgelauncher.composables.Btn
import com.tored.bridgelauncher.services.settings.SettingsState
import com.tored.bridgelauncher.services.settings.SettingsVM
import com.tored.bridgelauncher.services.settings.SystemBarAppearanceOptions
import com.tored.bridgelauncher.services.settings.settingsDataStore
import com.tored.bridgelauncher.ui.dirpicker.DirPickerDialogStateless
import com.tored.bridgelauncher.ui.dirpicker.DirPickerExportState
import com.tored.bridgelauncher.ui.dirpicker.DirPickerUIState
import com.tored.bridgelauncher.ui.dirpicker.Directory
import com.tored.bridgelauncher.ui.settings.sections.SettingsAboutSection
import com.tored.bridgelauncher.ui.settings.sections.SettingsBridgeSection
import com.tored.bridgelauncher.ui.settings.sections.SettingsDevelopmentSection
import com.tored.bridgelauncher.ui.settings.sections.SettingsOverlaysSection
import com.tored.bridgelauncher.ui.settings.sections.SettingsProjectSection
import com.tored.bridgelauncher.ui.settings.sections.SettingsSystemWallpaperSection
import com.tored.bridgelauncher.ui.theme.BridgeLauncherTheme
import com.tored.bridgelauncher.utils.exportForMockAsync
import com.tored.bridgelauncher.utils.getPrefKeyName
import com.tored.bridgelauncher.utils.showErrorToast
import com.tored.bridgelauncher.utils.writeDir
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties

@Composable
fun SettingsScreen(
    hasStoragePerms: Boolean,
    onGrantPermissionRequest: () -> Unit,
    vm: SettingsVM = viewModel(),
)
{
    val uiState by vm.settingsState.collectAsStateWithLifecycle()
    LaunchedEffect(vm) { vm.request() }

    var dirPickerCurrentDir by remember { mutableStateOf(uiState.currentProjDir) }
    var dirPickerFilterOrCreateDirText by remember { mutableStateOf("") }
    var dirPickerExportState by remember { mutableStateOf<DirPickerExportState?>(DirPickerExportState.NotExporting) }
    var dirPickerIsOpen by remember { mutableStateOf(false) }
    var dirPickerExportJob by remember { mutableStateOf<Job?>(null) }

    val context = LocalContext.current
    val bridge = context.applicationContext as BridgeLauncherApplication

    val scope = rememberCoroutineScope()

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
                        hasStoragePerms = hasStoragePerms,
                        onGrantPermissionRequest = onGrantPermissionRequest,
                        onChangeProjectDirRequest = {
                            dirPickerCurrentDir = uiState.currentProjDir
                            dirPickerExportState = null
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

                    SettingsDevelopmentSection(
                        onExportAppsRequest = {
                            dirPickerCurrentDir = uiState.lastMockExportDir ?: uiState.currentProjDir
                            dirPickerExportState = DirPickerExportState.NotExporting
                            dirPickerIsOpen = true
                        }
                    )

                    Divider()

                    SettingsAboutSection()

                    Divider()

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    )
                    {

                        var showDialog by remember { mutableStateOf(false) }
                        var resetInProgress by remember { mutableStateOf(false) }

                        Btn(
                            text = "Reset settings",
                            color = MaterialTheme.colors.error,
                        ) {
                            showDialog = true
                        }

                        if (showDialog)
                        {
                            AlertDialog(
                                onDismissRequest = { showDialog = false },
                                title = { Text("Confirm action") },
                                text = { Text("Are you sure you want to reset settings? This action cannot be undone.") },
                                confirmButton = {
                                    Btn(
                                        text = "Reset",
                                        color = MaterialTheme.colors.error,
                                        disabled = resetInProgress
                                    ) {
                                        resetInProgress = true

                                        try
                                        {
                                            vm.edit {

                                                // only reset properties that aren't tagged with @DontReset
                                                SettingsState::class.memberProperties
                                                    .filter { !it.hasAnnotation<DontReset>() }
                                                    .forEach {
                                                        this.remove(booleanPreferencesKey(getPrefKeyName(it)))
                                                    }

                                                Toast.makeText(context, "Settings reset.", Toast.LENGTH_SHORT).show()
                                                showDialog = false
                                            }
                                        }
                                        catch (ex: Exception)
                                        {
                                            context.showErrorToast(ex)
                                        }
                                        finally
                                        {
                                            resetInProgress = false
                                        }
                                    }
                                },
                                dismissButton = {
                                    Btn(text = "Cancel", color = MaterialTheme.colors.onSurface) {
                                        showDialog = false
                                    }
                                }
                            )
                        }
                    }
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

        DirPickerDialogStateless(
            modifier = Modifier
                .fillMaxSize(),
            uiState = if (dirPickerIsOpen)
            {
                if (hasStoragePerms)
                    DirPickerUIState.HasPermission(
                        currentDir = dirPickerCurrentDir ?: Environment.getExternalStorageDirectory(),
                        filterOrCreateDirText = dirPickerFilterOrCreateDirText,
                        exportState = dirPickerExportState,
                    )
                else
                    DirPickerUIState.NoPermission
            }
            else
            {
                null
            },
            onGrantPermissionRequest = onGrantPermissionRequest,
            onFilterOrCreateDirTextChange = { dirPickerFilterOrCreateDirText = it },
            onNavigateRequest = { dir ->
                dirPickerCurrentDir = dir
                dirPickerFilterOrCreateDirText = ""
            },
            onCreateDirRequest = {
                val dir = dirPickerCurrentDir
                val name = dirPickerFilterOrCreateDirText
                if (dir != null && name.isNotBlank())
                {
                    try
                    {
                        val newDir = Directory(dirPickerCurrentDir, name)
                        newDir.mkdir()
                        dirPickerCurrentDir = newDir
                        dirPickerFilterOrCreateDirText = ""
                    }
                    catch (ex: Exception)
                    {
                        context.showErrorToast(ex)
                    }
                }
            },
            onCancelRequest = {
                val job = dirPickerExportJob
                if (job == null)
                    dirPickerIsOpen = false
                else
                    job.cancel()
            },
            onConfirmRequest = { dir ->

                if (dirPickerExportState != null)
                {
                    dirPickerExportState = DirPickerExportState.Exporting()

                    dirPickerExportJob = scope.launch {

                        try
                        {
                            val exportToDir = dirPickerCurrentDir ?: Environment.getExternalStorageDirectory()

//                            exportForMockAsync(
//                                bridge.installedAppsHolder.installedApps.values,
//                                bridge.iconPacksHolder.iconPacksCache.values,
//                                exportToDir,
//                                onJobStarted = { startedCount ->
//                                    val exportState = dirPickerExportState
//                                    if (exportState is DirPickerExportState.Exporting)
//                                        dirPickerExportState = exportState.copy(startedCount = startedCount)
//                                },
//                                onJobFinished = { completedCount ->
//                                    val exportState = dirPickerExportState
//                                    if (exportState is DirPickerExportState.Exporting)
//                                        dirPickerExportState = exportState.copy(completedCount = completedCount)
//                                }
//                            )

                            context.settingsDataStore.edit {
                                it.writeDir(SettingsState::lastMockExportDir, exportToDir)
                            }

                            Toast.makeText(context, "Export finished!", Toast.LENGTH_SHORT).show()
                            dirPickerIsOpen = false
                        }
                        catch (ex: Exception)
                        {
                            context.showErrorToast(ex)
                        }
                        finally
                        {
                            dirPickerExportJob = null
                        }
                    }
                }
                else
                {
                    try
                    {
                        vm.edit {
                            writeDir(SettingsState::currentProjDir, dir)
                        }
                        dirPickerIsOpen = false
                    }
                    catch (ex: Exception)
                    {
                        context.showErrorToast(ex)
                    }
                }

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