package com.tored.bridgelauncher.ui2.settings

import android.app.Application
import android.app.StatusBarManager
import android.graphics.drawable.Icon
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
import com.tored.bridgelauncher.services.BridgeServiceProvider
import com.tored.bridgelauncher.services.PermsManager
import com.tored.bridgelauncher.services.apps.InstalledAppsHolder
import com.tored.bridgelauncher.services.iconpacks.InstalledIconPacksHolder
import com.tored.bridgelauncher.services.settings.SettingsState
import com.tored.bridgelauncher.services.settings.SettingsVM
import com.tored.bridgelauncher.services.settings.settingsDataStore
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
import com.tored.bridgelauncher.utils.tryStartWallpaperPickerActivity
import com.tored.bridgelauncher.utils.writeBool
import com.tored.bridgelauncher.utils.writeEnum
import kotlinx.coroutines.launch

private val TAG = SettingsScreenVM::class.simpleName

class SettingsScreenVM(
    private val _app: BridgeLauncherApplication,
    private val _settingsVM: SettingsVM,
    private val _permsManager: PermsManager,
    private val _apps: InstalledAppsHolder,
    private val _iconPacks: InstalledIconPacksHolder,
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
        changeProject = { TODO() },
        changeAllowProjectsToTurnScreenOff = { updateSettings { writeBool(SettingsState::allowProjectsToTurnScreenOff, it) } },
        onStoragePermsStateChanged = { _permsManager.notifyPermsMightHaveChanged() },
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
                _statusBarManager!!.requestAddTileService(
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
            isExportDisabled = false
        )
    }

    val developmentSectionActions = SettingsScreen2DevelopmentSectionActions(
        exportMockFolder = { TODO() }
    )


    // utils

    private fun updateSettings(edits: MutablePreferences.() -> Unit)
    {
        viewModelScope.launch {
            _app.settingsDataStore.edit { it.edits() }
        }
    }


    companion object
    {
        fun from(context: Application, serviceProvider: BridgeServiceProvider): SettingsScreenVM
        {
            with(serviceProvider)
            {
                return SettingsScreenVM(
                    _app = context.bridgeLauncherApplication,
                    _permsManager = storagePermsManager,
                    _settingsVM = settingsVM,
                    _apps = installedAppsHolder,
                    _iconPacks = installedIconPacksHolder,
                )
            }
        }

        // https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-factories
        val Factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as BridgeLauncherApplication
                from(app, app.serviceProvider)
            }
        }
    }
}