package com.tored.bridgelauncher.settings

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tored.bridgelauncher.utils.readBool
import com.tored.bridgelauncher.utils.readDir
import com.tored.bridgelauncher.utils.readEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

val Context.settingsDataStore by preferencesDataStore("settings")

@HiltViewModel
class SettingsVM @Inject constructor(
    @ApplicationContext appContext: Context
) : ViewModel()
{
    private val _ds = appContext.settingsDataStore

    fun request()
    {
        viewModelScope.launch {
            _ds.data.collectLatest { prefs ->
                _settingsUIState.update {
                    it.copy(
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

    private val _settingsUIState = MutableStateFlow(SettingsState())
    val settingsUIState = _settingsUIState.asStateFlow()
}

