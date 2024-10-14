package com.tored.bridgelauncher.ui2.settings

import android.app.Application
import androidx.compose.runtime.derivedStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.tored.bridgelauncher.BridgeLauncherApplication
import com.tored.bridgelauncher.services.BridgeServiceProvider
import com.tored.bridgelauncher.services.PermsManager
import com.tored.bridgelauncher.services.apps.InstalledAppsHolder
import com.tored.bridgelauncher.services.iconpacks.InstalledIconPacksHolder
import com.tored.bridgelauncher.services.settings.SettingsVM
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

private val TAG = SettingsScreenVM::class.simpleName

class SettingsScreenVM(
    private val _context: Application,
    private val _settingsVM: SettingsVM,
    private val _permsManager: PermsManager,
    private val _apps: InstalledAppsHolder,
    private val _iconPacks: InstalledIconPacksHolder,
) : ViewModel()
{

    // PROJECT

    val projectSectionState = derivedStateOf()
    {
        val settings = _settingsVM.settingsState.value

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
        requestGrantStoragePerms = { TODO() },
        changeAllowProjectsToTurnScreenOff = { TODO() },
    )


    // WALLPAPER

    val wallpaperSectionState = derivedStateOf()
    {
        val settings = _settingsVM.settingsState.value
        SettingsScreen2WallpaperSectionState(
            drawSystemWallpaperBehindWebView = settings.drawSystemWallpaperBehindWebView,
        )
    }

    val wallpaperSectionActions = SettingsScreen2WallpaperSectionActions(
        changeSystemWallpaper = { TODO() },
        changeDrawSystemWallpaperBehindWebView = { TODO() }
    )


    // OVERLAYS

    val overlaysSectionState = derivedStateOf()
    {
        val settings = _settingsVM.settingsState.value
        SettingsScreen2OverlaysSectionState(
            statusBarAppearance = settings.statusBarAppearance,
            navigationBarAppearance = settings.navigationBarAppearance,
            drawWebViewOverscrollEffects = settings.drawWebViewOverscrollEffects,
        )
    }

    val overlaysSectionActions = SettingsScreen2OverlaysSectionActions(
        changeStatusBarAppearance = { TODO() },
        changeNavigationBarAppearance = { TODO() },
        changeDrawWebViewOverscrollEffects = { TODO() },
    )


    // BRIDGE

    val bridgeSectionState = derivedStateOf()
    {
        val settings = _settingsVM.settingsState.value
        SettingsScreen2BridgeSectionState(
            theme = settings.theme,
            showBridgeButton = settings.showBridgeButton,
            showLaunchAppsWhenBridgeButtonCollapsed = settings.showLaunchAppsWhenBridgeButtonCollapsed,
            isQSTileAdded = settings.isQSTileAdded,
            isQSTilePromptSupported = CurrentAndroidVersion.supportsQSTilePrompt()
        )
    }

    val bridgeSectionActions = SettingsScreen2BridgeSectionActions(
        changeTheme = { TODO() },
        changeShowBridgeButton = { TODO() },
        changeShowLaunchAppsWhenBridgeButtonCollapsed = { TODO() },
        requestQSTilePrompt = { TODO() },
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


    companion object
    {
        fun from(context: Application, serviceProvider: BridgeServiceProvider): SettingsScreenVM
        {
            with(serviceProvider)
            {
                return SettingsScreenVM(
                    _context = context,
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