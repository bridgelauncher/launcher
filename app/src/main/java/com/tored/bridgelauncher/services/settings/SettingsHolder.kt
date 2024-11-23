package com.tored.bridgelauncher.services.settings

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.tored.bridgelauncher.BridgeLauncherApplication
import com.tored.bridgelauncher.utils.readBool
import com.tored.bridgelauncher.utils.readDir
import com.tored.bridgelauncher.utils.readEnum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

private val TAG = SettingsHolder::class.simpleName

val Context.settingsDataStore by preferencesDataStore(
    name = "settings",
)

class SettingsHolder(
    private val _app: BridgeLauncherApplication,
)
{
    private val _coroutineScope = CoroutineScope(Dispatchers.IO) + SupervisorJob()

    private val _ds = _app.settingsDataStore

    private val _settingsState = MutableStateFlow(SettingsState())
    val settingsState = _settingsState.asStateFlow()

    fun startCollectingSettingsUpdates()
    {
        _coroutineScope.launch {
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
        _coroutineScope.launch {
            _ds.edit(transform)
        }
    }
}
