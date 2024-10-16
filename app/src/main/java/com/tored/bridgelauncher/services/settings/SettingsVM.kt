package com.tored.bridgelauncher.services.settings

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.tored.bridgelauncher.BridgeLauncherApplication
import com.tored.bridgelauncher.utils.readBool
import com.tored.bridgelauncher.utils.readDir
import com.tored.bridgelauncher.utils.readEnum
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "SettingsVM"

val Context.settingsDataStore by preferencesDataStore("settings")

class SettingsVM(app: BridgeLauncherApplication) : AndroidViewModel(app)
{
    private val _ds = app.settingsDataStore

    fun request()
    {
        viewModelScope.launch {
            _ds.data.collectLatest { prefs ->
                try
                {
                    Log.d(TAG, "_ds.data.collectLatest(): preferences changed, updating settingsState")
                    val newState = SettingsState(
                        currentProjDir = prefs.readDir(SettingsState::currentProjDir),
                        lastMockExportDir = prefs.readDir(SettingsState::lastMockExportDir),

                        isQSTileAdded = prefs.readBool(SettingsState::isQSTileAdded, false),
                        isDeviceAdminEnabled = prefs.readBool(SettingsState::isDeviceAdminEnabled, false),
                        isExternalStorageManager = prefs.readBool(SettingsState::isExternalStorageManager, false),
                        isAccessibilityServiceEnabled = prefs.readBool(SettingsState::isAccessibilityServiceEnabled, false),

                        theme = prefs.readEnum(SettingsState::theme, ThemeOptions.System),

                        allowProjectsToTurnScreenOff = prefs.readBool(SettingsState::allowProjectsToTurnScreenOff, false),
                        drawSystemWallpaperBehindWebView = prefs.readBool(SettingsState::drawSystemWallpaperBehindWebView, true),

                        statusBarAppearance = prefs.readEnum(SettingsState::statusBarAppearance, SystemBarAppearanceOptions.LightIcons),
                        navigationBarAppearance = prefs.readEnum(SettingsState::navigationBarAppearance, SystemBarAppearanceOptions.LightIcons),

                        drawWebViewOverscrollEffects = prefs.readBool(SettingsState::drawWebViewOverscrollEffects, false),
                        showBridgeButton = prefs.readBool(SettingsState::showBridgeButton, true),
                        showLaunchAppsWhenBridgeButtonCollapsed = prefs.readBool(SettingsState::showLaunchAppsWhenBridgeButtonCollapsed, false),
                    )
                    _settingsState.value = newState
                }
                catch (err: Error)
                {
                    Log.e(TAG, "_ds.data.collectLatest(): crashed when reading settings", err)
                    throw err
                }

            }
        }
    }

    fun edit(transform: suspend MutablePreferences.() -> Unit)
    {
        viewModelScope.launch {
            _ds.edit(transform)
        }
    }

    private val _settingsState = MutableStateFlow(SettingsState())
    val settingsState = _settingsState.asStateFlow()


    companion object
    {
        // https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-factories
        val Factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) as BridgeLauncherApplication
                app.serviceProvider.settingsVM
            }
        }
    }
}
