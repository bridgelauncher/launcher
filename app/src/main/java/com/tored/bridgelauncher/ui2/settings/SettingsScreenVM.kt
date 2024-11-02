package com.tored.bridgelauncher.ui2.settings

import android.app.Application
import android.app.StatusBarManager
import android.graphics.drawable.Icon
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.tored.bridgelauncher.BridgeLauncherApplication
import com.tored.bridgelauncher.R
import com.tored.bridgelauncher.services.BridgeServices
import com.tored.bridgelauncher.services.mockexport.MockExportProgressState
import com.tored.bridgelauncher.services.mockexport.MockExporter
import com.tored.bridgelauncher.services.perms.PermsManager
import com.tored.bridgelauncher.services.settings.SettingsState
import com.tored.bridgelauncher.services.settings.SettingsVM
import com.tored.bridgelauncher.services.settings.settingsDataStore
import com.tored.bridgelauncher.ui2.dirpicker.DirectoryPickerActions
import com.tored.bridgelauncher.ui2.dirpicker.DirectoryPickerMode
import com.tored.bridgelauncher.ui2.dirpicker.DirectoryPickerRealDirectory
import com.tored.bridgelauncher.ui2.dirpicker.DirectoryPickerState
import com.tored.bridgelauncher.ui2.progressdialog.MockExportProgressDialogActions
import com.tored.bridgelauncher.ui2.settings.sections.bridge.SettingsScreen2BridgeSectionActions
import com.tored.bridgelauncher.ui2.settings.sections.bridge.SettingsScreen2BridgeSectionState
import com.tored.bridgelauncher.ui2.settings.sections.development.SettingsScreen2DevelopmentSectionActions
import com.tored.bridgelauncher.ui2.settings.sections.development.SettingsScreen2DevelopmentSectionState
import com.tored.bridgelauncher.ui2.settings.sections.overlays.SettingsScreen2OverlaysSectionActions
import com.tored.bridgelauncher.ui2.settings.sections.overlays.SettingsScreen2OverlaysSectionState
import com.tored.bridgelauncher.ui2.settings.sections.project.ScreenLockingMethodOptions
import com.tored.bridgelauncher.ui2.settings.sections.project.SettingsScreen2ProjectSectionActions
import com.tored.bridgelauncher.ui2.settings.sections.project.SettingsScreen2ProjectSectionState
import com.tored.bridgelauncher.ui2.settings.sections.project.SettingsScreen2ProjectSectionStateProjectInfo
import com.tored.bridgelauncher.ui2.settings.sections.wallpaper.SettingsScreen2WallpaperSectionActions
import com.tored.bridgelauncher.ui2.settings.sections.wallpaper.SettingsScreen2WallpaperSectionState
import com.tored.bridgelauncher.utils.CurrentAndroidVersion
import com.tored.bridgelauncher.utils.bridgeLauncherApplication
import com.tored.bridgelauncher.utils.collectAsStateButInViewModel
import com.tored.bridgelauncher.utils.tryOrShowErrorToast
import com.tored.bridgelauncher.utils.tryStartWallpaperPickerActivity
import com.tored.bridgelauncher.utils.writeBool
import com.tored.bridgelauncher.utils.writeDir
import com.tored.bridgelauncher.utils.writeEnum
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import kotlin.test.assertNotNull

private val TAG = SettingsScreenVM::class.simpleName

class SettingsScreenVM(
    private val _app: BridgeLauncherApplication,
    private val _settingsVM: SettingsVM,
    private val _permsManager: PermsManager,
    private val _mockExporter: MockExporter,
) : ViewModel()
{
    private val _statusBarManager = if (CurrentAndroidVersion.supportsQSTilePrompt())
        _app.getSystemService(StatusBarManager::class.java)
    else
        null

    val settingsState by collectAsStateButInViewModel(_settingsVM.settingsState)


    // PROJECT

    val projectSectionState = derivedStateOf()
    {
        val settings = settingsState

        val screenLockingMethod = when (CurrentAndroidVersion.supportsAccessiblityServiceScreenLock())
        {
            true -> ScreenLockingMethodOptions.AccessibilityService
            false -> ScreenLockingMethodOptions.DeviceAdmin
        }

        SettingsScreen2ProjectSectionState(
            projectInfo = settings.currentProjDir?.run {
                SettingsScreen2ProjectSectionStateProjectInfo(
                    name = name
                )
            },
            hasStoragePerms = _permsManager.hasStoragePermsState.value,
            allowProjectsToTurnScreenOff = settings.allowProjectsToTurnScreenOff,
            screenLockingMethod = screenLockingMethod,
            canBridgeTurnScreenOff = when (screenLockingMethod)
            {
                ScreenLockingMethodOptions.DeviceAdmin -> _settingsVM.settingsState.value.isDeviceAdminEnabled
                ScreenLockingMethodOptions.AccessibilityService -> _settingsVM.settingsState.value.isAccessibilityServiceEnabled
            }
        )
    }

    val projectSectionActions = SettingsScreen2ProjectSectionActions(
        changeProject = { openDirectoryPicker(DirectoryPickerMode.LoadProject) },
        changeAllowProjectsToTurnScreenOff = { updateSettings { writeBool(SettingsState::allowProjectsToTurnScreenOff, it) } },
    )


    // WALLPAPER

    val wallpaperSectionState = derivedStateOf()
    {
        val settings = settingsState
        SettingsScreen2WallpaperSectionState(
            drawSystemWallpaperBehindWebView = settings.drawSystemWallpaperBehindWebView,
        )
    }

    val wallpaperSectionActions = SettingsScreen2WallpaperSectionActions(
        changeSystemWallpaper = { _app.tryStartWallpaperPickerActivity() },
        changeDrawSystemWallpaperBehindWebView = { updateSettings { writeBool(SettingsState::drawSystemWallpaperBehindWebView, it) } }
    )


    // OVERLAYS

    val overlaysSectionState = derivedStateOf()
    {
        val settings = settingsState
        Log.d(TAG, "overlaysSectionState reevaluating")
        SettingsScreen2OverlaysSectionState(
            statusBarAppearance = settings.statusBarAppearance,
            navigationBarAppearance = settings.navigationBarAppearance,
            drawWebViewOverscrollEffects = settings.drawWebViewOverscrollEffects,
        )
    }

    val overlaysSectionActions = SettingsScreen2OverlaysSectionActions(
        changeStatusBarAppearance = {
            Log.d(TAG, "changeStatusBarAppearance called")
            updateSettings { writeEnum(SettingsState::statusBarAppearance, it) }
        },
        changeNavigationBarAppearance = {
            Log.d(TAG, "changeNavigationBarAppearance called")
            updateSettings { writeEnum(SettingsState::navigationBarAppearance, it) }
        },
        changeDrawWebViewOverscrollEffects = { updateSettings { writeBool(SettingsState::drawWebViewOverscrollEffects, it) } },
    )


    // BRIDGE

    val bridgeSectionState = derivedStateOf()
    {
        val settings = settingsState
        SettingsScreen2BridgeSectionState(
            theme = settings.theme,
            showBridgeButton = settings.showBridgeButton,
            showLaunchAppsWhenBridgeButtonCollapsed = settings.showLaunchAppsWhenBridgeButtonCollapsed,
            isQSTileAdded = settings.isQSTileAdded,
            isQSTilePromptSupported = CurrentAndroidVersion.supportsQSTilePrompt()
        )
    }

    val bridgeSectionActions = SettingsScreen2BridgeSectionActions(
        changeTheme = { updateSettings { writeEnum(SettingsState::theme, it) } },
        changeShowBridgeButton = { updateSettings { writeBool(SettingsState::showBridgeButton, it) } },
        changeShowLaunchAppsWhenBridgeButtonCollapsed = { updateSettings { writeBool(SettingsState::showLaunchAppsWhenBridgeButtonCollapsed, it) } },
        requestQSTilePrompt = {
            if (CurrentAndroidVersion.supportsQSTilePrompt())
            {
                assertNotNull(_statusBarManager, "Current Android version supports QS tile prompt, but statusBarManager was null.")
                _statusBarManager.requestAddTileService(
                    _app.qsTileServiceComponentName,
                    "Bridge button",
                    Icon.createWithResource(_app, R.drawable.ic_bridge_white),
                    {},
                    {}
                )
            }
        },
    )


    // DEVELOPMENT

    val developmentSectionState = derivedStateOf {
        SettingsScreen2DevelopmentSectionState(
            // TODO: disabled until apps, icons and icon packs are done loading?
            isExportDisabled = false,
        )
    }

    val developmentSectionActions = SettingsScreen2DevelopmentSectionActions(
        exportMockFolder = { openDirectoryPicker(DirectoryPickerMode.MockExport) }
    )


    // DIRECTORY PICKER

    private val _directoryPickerStateFlow = MutableStateFlow<DirectoryPickerState?>(null)
    val directoryPickerState = collectAsStateButInViewModel(_directoryPickerStateFlow)

    private var _mockExportJob: Job? = null

    private val _mockExportProgressStateFlow = MutableStateFlow<MockExportProgressState?>(null)
    val mockExportProgressState = collectAsStateButInViewModel(_mockExportProgressStateFlow)

    val mockExportProgressDialogActions = MockExportProgressDialogActions(
        dismiss = {
            _mockExportJob?.cancel()
            _mockExportJob = null
        }
    )

    private fun currentProjDirOrDefault() = settingsState.currentProjDir.let {
        if (it?.canRead() == true)
            it
        else
            Environment.getExternalStorageDirectory()
    }

    private fun lastMockExportDirOrDefault() = settingsState.lastMockExportDir.let {
        if (it?.canRead() == true)
            it
        else
            currentProjDirOrDefault()
    }

    private fun observeStoragePermissionState()
    {
        viewModelScope.launch {
            _permsManager.hasStoragePermsState.collectLatest { hasStoragePermission ->
                _directoryPickerStateFlow.value.let { currState ->
                    Log.d(TAG, "observeStoragePermissionState: _permsManager.hasStoragePermsState.collectLatest called, hasStoragePermission = $hasStoragePermission, currState = $currState")
                    if (hasStoragePermission)
                    {
                        if (currState is DirectoryPickerState.NoStoragePermission)
                        {
                            // permission granted while dialog open
                            Log.d(TAG, "observeStoragePermissionState: NoStoragePermission -> HasStoragePermission")
                            _directoryPickerStateFlow.value = DirectoryPickerState.HasStoragePermission.fromDirectoryAndFilter(
                                mode = currState.mode,
                                directory = currentProjDirOrDefault(),
                            )
                        }
                    }
                    else
                    {
                        if (currState is DirectoryPickerState.HasStoragePermission)
                        {
                            // permission revoked while dialog open
                            Log.d(TAG, "observeStoragePermissionState: HasStoragePermission -> NoStoragePermission")
                            _directoryPickerStateFlow.value = DirectoryPickerState.NoStoragePermission(currState.mode)
                        }
                    }
                }
            }
        }
    }

    private fun openDirectoryPicker(mode: DirectoryPickerMode)
    {
        if (_permsManager.hasStoragePermsState.value)
        {
            _directoryPickerStateFlow.value = DirectoryPickerState.HasStoragePermission.fromDirectoryAndFilter(
                mode = mode,
                directory = when (mode)
                {
                    DirectoryPickerMode.LoadProject -> currentProjDirOrDefault()
                    DirectoryPickerMode.MockExport -> lastMockExportDirOrDefault()
                }
            )
        }
        else
        {
            _directoryPickerStateFlow.value = DirectoryPickerState.NoStoragePermission(mode)
        }
    }

    val directoryPickerActions = DirectoryPickerActions(
        dismiss = { _directoryPickerStateFlow.value = null },
        navigateToDirectory = {
            _directoryPickerStateFlow.value.let { currState ->
                if (currState is DirectoryPickerState.HasStoragePermission && it is DirectoryPickerRealDirectory && it.file.canRead())
                {
                    _directoryPickerStateFlow.value = DirectoryPickerState.HasStoragePermission.fromDirectoryAndFilter(
                        mode = currState.mode,
                        directory = it.file,
                    )
                }
            }
        },
        selectCurrentDirectory = {
            _directoryPickerStateFlow.value.let { currState ->
                if (currState is DirectoryPickerState.HasStoragePermission && currState.currentDirectory is DirectoryPickerRealDirectory)
                {
                    when (currState.mode)
                    {
                        DirectoryPickerMode.LoadProject -> updateSettings { writeDir(SettingsState::currentProjDir, currState.currentDirectory.file) }
                        DirectoryPickerMode.MockExport -> if (_mockExportJob == null)
                        {
                            // hide directory picker
                            _directoryPickerStateFlow.value = null
                            // start exporting
                            _mockExportProgressStateFlow.value = MockExportProgressState()

                            _mockExportJob = viewModelScope.launch {
                                _mockExporter.exportToDirectory(currState.currentDirectory.file, _mockExportProgressStateFlow)
                                _app.settingsDataStore.edit { it.writeDir(SettingsState::lastMockExportDir, currState.currentDirectory.file) }
                                _mockExportProgressStateFlow.value = null
                                _mockExportJob = null
                            }
                        }
                    }
                }
            }
        },
        requestFilterOrCreateDirectoryTextChange = { newText ->
            _directoryPickerStateFlow.value.let { currState ->
                if (currState is DirectoryPickerState.HasStoragePermission && currState.currentDirectory is DirectoryPickerRealDirectory)
                {
                    _directoryPickerStateFlow.value = DirectoryPickerState.HasStoragePermission.fromDirectoryAndFilter(
                        mode = currState.mode,
                        directory = currState.currentDirectory.file,
                        filterOrCreateDirectoryText = newText,
                    )
                }
            }
        },
        createSubdirectory = {
            _directoryPickerStateFlow.value.let { currState ->
                if (currState is DirectoryPickerState.HasStoragePermission && currState.currentDirectory is DirectoryPickerRealDirectory)
                {
                    if (currState.filterOrCreateDirectoryText.isNotBlank())
                    {
                        _app.tryOrShowErrorToast {
                            val newDir = File(currState.currentDirectory.file, currState.filterOrCreateDirectoryText)
                            newDir.mkdirs()
                        }
                    }

                    // refresh
                    _directoryPickerStateFlow.value = DirectoryPickerState.HasStoragePermission.fromDirectoryAndFilter(
                        mode = currState.mode,
                        directory = currState.currentDirectory.file,
                        filterOrCreateDirectoryText = currState.filterOrCreateDirectoryText,
                    )
                }
            }
        },
    )


    // MISC ACTIONS

    val miscActions = SettingsScreen2MiscActions(
        permissionsChanged = { _permsManager.notifyPermsMightHaveChanged() },
    )

    // utils

    private fun updateSettings(edits: MutablePreferences.() -> Unit)
    {
        viewModelScope.launch {
            _app.settingsDataStore.edit { it.edits() }
        }
    }

    init
    {
        observeStoragePermissionState()
    }

    companion object
    {
        fun from(context: Application, serviceProvider: BridgeServices): SettingsScreenVM
        {
            with(serviceProvider)
            {
                return SettingsScreenVM(
                    _app = context.bridgeLauncherApplication,
                    _permsManager = storagePermsManager,
                    _settingsVM = settingsVM,
                    _mockExporter = mockExporter,
                )
            }
        }

        // https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-factories
        val Factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as BridgeLauncherApplication
                from(app, app.services)
            }
        }
    }
}