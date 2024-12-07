package com.tored.bridgelauncher.ui2.settings

import android.app.Application
import android.app.StatusBarManager
import android.graphics.drawable.Icon
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.tored.bridgelauncher.services.perms.PermsHolder
import com.tored.bridgelauncher.services.settings2.BridgeSettings
import com.tored.bridgelauncher.services.settings2.ResettableBridgeSettings
import com.tored.bridgelauncher.services.settings2.resetBridgeSetting
import com.tored.bridgelauncher.services.settings2.setBridgeSetting
import com.tored.bridgelauncher.services.settings2.settingsDataStore
import com.tored.bridgelauncher.services.settings2.useBridgeSettingState
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
import com.tored.bridgelauncher.ui2.settings.sections.reset.SettingsScreen2ResetSectionActions
import com.tored.bridgelauncher.ui2.settings.sections.reset.SettingsScreen2ResetSectionState
import com.tored.bridgelauncher.ui2.settings.sections.wallpaper.SettingsScreen2WallpaperSectionActions
import com.tored.bridgelauncher.ui2.settings.sections.wallpaper.SettingsScreen2WallpaperSectionState
import com.tored.bridgelauncher.utils.CurrentAndroidVersion
import com.tored.bridgelauncher.utils.bridgeLauncherApplication
import com.tored.bridgelauncher.utils.collectAsStateButInViewModel
import com.tored.bridgelauncher.utils.suspendTryOrShowErrorToast
import com.tored.bridgelauncher.utils.tryOrShowErrorToast
import com.tored.bridgelauncher.utils.tryStartWallpaperPickerActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import kotlin.test.assertNotNull

private val TAG = SettingsScreenVM::class.simpleName

class SettingsScreenVM(
    private val _app: BridgeLauncherApplication,
    private val _permsHolder: PermsHolder,
    private val _mockExporter: MockExporter,
) : ViewModel()
{
    private val _statusBarManager = if (CurrentAndroidVersion.supportsQSTilePrompt())
        _app.getSystemService(StatusBarManager::class.java)
    else
        null

    // SETTING STATES

    private val _isDeviceAdminEnabled by useBridgeSettingState(_app, BridgeSettings.isDeviceAdminEnabled)
    private val _isAccessibilityServiceEnabled by useBridgeSettingState(_app, BridgeSettings.isAccessibilityServiceEnabled)
    private val _isQSTileAdded by useBridgeSettingState(_app, BridgeSettings.isQSTileAdded)

    private val _currentProjDir by useBridgeSettingState(_app, BridgeSettings.currentProjDir)
    private val _lastMockExportDir by useBridgeSettingState(_app, BridgeSettings.lastMockExportDir)

    private val _theme by useBridgeSettingState(_app, BridgeSettings.theme)
    private val _allowProjectsToTurnScreenOff by useBridgeSettingState(_app, BridgeSettings.allowProjectsToTurnScreenOff)
    private val _drawSystemWallpaperBehindWebView by useBridgeSettingState(_app, BridgeSettings.drawSystemWallpaperBehindWebView)
    private val _statusBarAppearance by useBridgeSettingState(_app, BridgeSettings.statusBarAppearance)
    private val _navigationBarAppearance by useBridgeSettingState(_app, BridgeSettings.navigationBarAppearance)
    private val _drawWebViewOverscrollEffects by useBridgeSettingState(_app, BridgeSettings.drawWebViewOverscrollEffects)
    private val _showBridgeButton by useBridgeSettingState(_app, BridgeSettings.showBridgeButton)
    private val _showLaunchAppsWhenBridgeButtonCollapsed by useBridgeSettingState(_app, BridgeSettings.showLaunchAppsWhenBridgeButtonCollapsed)

    // PROJECT

    val projectSectionState = derivedStateOf()
    {
        val screenLockingMethod = when (CurrentAndroidVersion.supportsAccessiblityServiceScreenLock())
        {
            true -> ScreenLockingMethodOptions.AccessibilityService
            false -> ScreenLockingMethodOptions.DeviceAdmin
        }

        SettingsScreen2ProjectSectionState(
            projectInfo = _currentProjDir?.run {
                SettingsScreen2ProjectSectionStateProjectInfo(
                    name = name
                )
            },
            hasStoragePerms = _permsHolder.hasStoragePermsState.value,
            allowProjectsToTurnScreenOff = _allowProjectsToTurnScreenOff,
            screenLockingMethod = screenLockingMethod,
            canBridgeTurnScreenOff = when (screenLockingMethod)
            {
                ScreenLockingMethodOptions.DeviceAdmin -> _isDeviceAdminEnabled
                ScreenLockingMethodOptions.AccessibilityService -> _isAccessibilityServiceEnabled
            }
        )
    }

    val projectSectionActions = SettingsScreen2ProjectSectionActions(
        changeProject = { openDirectoryPicker(DirectoryPickerMode.LoadProject) },
        changeAllowProjectsToTurnScreenOff = { updateSettings { setBridgeSetting(BridgeSettings.allowProjectsToTurnScreenOff, it) } },
    )


    // WALLPAPER

    val wallpaperSectionState = derivedStateOf()
    {
        SettingsScreen2WallpaperSectionState(
            drawSystemWallpaperBehindWebView = _drawSystemWallpaperBehindWebView,
        )
    }

    val wallpaperSectionActions = SettingsScreen2WallpaperSectionActions(
        changeSystemWallpaper = { _app.tryStartWallpaperPickerActivity() },
        changeDrawSystemWallpaperBehindWebView = { updateSettings { setBridgeSetting(BridgeSettings.drawSystemWallpaperBehindWebView, it) } }
    )


    // OVERLAYS

    val overlaysSectionState = derivedStateOf()
    {
        Log.d(TAG, "overlaysSectionState reevaluating")
        SettingsScreen2OverlaysSectionState(
            statusBarAppearance = _statusBarAppearance,
            navigationBarAppearance = _navigationBarAppearance,
            drawWebViewOverscrollEffects = _drawWebViewOverscrollEffects,
        )
    }

    val overlaysSectionActions = SettingsScreen2OverlaysSectionActions(
        changeStatusBarAppearance = {
            Log.d(TAG, "changeStatusBarAppearance called")
            updateSettings { setBridgeSetting(BridgeSettings.statusBarAppearance, it) }
        },
        changeNavigationBarAppearance = {
            Log.d(TAG, "changeNavigationBarAppearance called")
            updateSettings { setBridgeSetting(BridgeSettings.navigationBarAppearance, it) }
        },
        changeDrawWebViewOverscrollEffects = {
            Log.d(TAG, "changeDrawWebViewOverscrollEffects called: $it")
            updateSettings { setBridgeSetting(BridgeSettings.drawWebViewOverscrollEffects, it) }
        },
    )


    // BRIDGE

    val bridgeSectionState = derivedStateOf()
    {
        SettingsScreen2BridgeSectionState(
            theme = _theme,
            showBridgeButton = _showBridgeButton,
            showLaunchAppsWhenBridgeButtonCollapsed = _showLaunchAppsWhenBridgeButtonCollapsed,
            isQSTileAdded = _isQSTileAdded,
            isQSTilePromptSupported = CurrentAndroidVersion.supportsQSTilePrompt()
        )
    }

    val bridgeSectionActions = SettingsScreen2BridgeSectionActions(
        changeTheme = { updateSettings { setBridgeSetting(BridgeSettings.theme, it) } },
        changeShowBridgeButton = { updateSettings { setBridgeSetting(BridgeSettings.showBridgeButton, it) } },
        changeShowLaunchAppsWhenBridgeButtonCollapsed = { updateSettings { setBridgeSetting(BridgeSettings.showLaunchAppsWhenBridgeButtonCollapsed, it) } },
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

    private fun currentProjDirOrDefault() = _currentProjDir.let {
        if (it?.canRead() == true)
            it
        else
            Environment.getExternalStorageDirectory()
    }

    private fun lastMockExportDirOrDefault() = _lastMockExportDir.let {
        if (it?.canRead() == true)
            it
        else
            currentProjDirOrDefault()
    }

    private fun observeStoragePermissionState()
    {
        viewModelScope.launch {
            _permsHolder.hasStoragePermsState.collectLatest { hasStoragePermission ->
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
        if (_permsHolder.hasStoragePermsState.value)
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
                        DirectoryPickerMode.LoadProject ->
                        {
                            // hide directory picker
                            _directoryPickerStateFlow.value = null
                            updateSettings { setBridgeSetting(BridgeSettings.currentProjDir, currState.currentDirectory.file) }
                        }

                        DirectoryPickerMode.MockExport -> if (_mockExportJob == null)
                        {
                            // hide directory picker
                            _directoryPickerStateFlow.value = null
                            // start exporting
                            _mockExportProgressStateFlow.value = MockExportProgressState()

                            _mockExportJob = viewModelScope.launch {
                                _mockExporter.exportToDirectory(currState.currentDirectory.file, _mockExportProgressStateFlow)
                                _app.settingsDataStore.edit { it.setBridgeSetting(BridgeSettings.lastMockExportDir, currState.currentDirectory.file) }
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

    // RESET SETTINGS

    private val _isSettingsResetInProgressState = mutableStateOf(false)

    val resetSectionActions = SettingsScreen2ResetSectionActions(
        onResetRequest = {
            val isResetInProgress = _isSettingsResetInProgressState.value
            if (!isResetInProgress)
            {
                viewModelScope.launch {
                    _isSettingsResetInProgressState.value = true

                    _app.suspendTryOrShowErrorToast {
                        _app.settingsDataStore.edit { prefs ->
                            ResettableBridgeSettings.forEach { prefs.resetBridgeSetting(it) }
                        }
                        Toast.makeText(_app, "Reset finished.", Toast.LENGTH_SHORT).show()
                    }

                    _isSettingsResetInProgressState.value = false
                }
            }
        },
    )

    val resetSectionState = derivedStateOf {
        SettingsScreen2ResetSectionState(
            isResetInProgress = _isSettingsResetInProgressState.value,
        )
    }


    // MISC ACTIONS

    val miscActions = SettingsScreen2MiscActions(
        permissionsChanged = { _permsHolder.notifyPermsMightHaveChanged() },
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
                    _permsHolder = storagePermsHolder,
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