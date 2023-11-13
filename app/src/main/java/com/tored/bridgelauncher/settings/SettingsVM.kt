package com.tored.bridgelauncher.vms

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tored.bridgelauncher.SystemBarAppearanceOptions
import com.tored.bridgelauncher.ThemeOptions
import com.tored.bridgelauncher.annotations.Display
import com.tored.bridgelauncher.settings.SettingsManager
import com.tored.bridgelauncher.utils.intToEnumOrDefault
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUIState(

    val theme: ThemeOptions = ThemeOptions.System,

    val currentProjName: String = "Test Launcher",

    @Display("Allow projects to turn the screen off")
    val allowProjectsToTurnScreenOff: Boolean = false,

    @Display("Draw system wallpaper behind WebView")
    val drawSystemWallpaperBehindWebView: Boolean = true,

    @Display("Status bar")
    val statusBarAppearance: SystemBarAppearanceOptions = SystemBarAppearanceOptions.DarkIcons,

    @Display("Navigation bar")
    val navigationBarAppearance: SystemBarAppearanceOptions = SystemBarAppearanceOptions.DarkIcons,

    @Display("Draw WebView overscroll effects")
    val drawWebViewOverscrollEffects: Boolean = false,

    @Display("Show Bridge button")
    val showBridgeButton: Boolean = true,

    @Display("Show Launch apps button when the Bridge menu is collapsed")
    val showLaunchAppsWhenBridgeButtonCollapsed: Boolean = false,
)

@HiltViewModel
class SettingsVM @Inject constructor(
    val settingsManager: SettingsManager
) : ViewModel()
{
    private val _themeKey = intPreferencesKey(SettingsUIState::theme.name)
    fun request()
    {
        viewModelScope.launch {
            settingsManager.store.data.collectLatest { prefs ->
                _settingsUIState.update {
                    it.copy(
                        theme = intToEnumOrDefault(prefs[_themeKey], ThemeOptions.System)
                    )
                }
            }
        }
    }

    fun switchTheme(theme: ThemeOptions)
    {
        viewModelScope.launch {
            settingsManager.store.edit {
                it[_themeKey] = theme.rawValue
            }
        }
    }

    private val _settingsUIState = MutableStateFlow(SettingsUIState())
    val settingsUIState = _settingsUIState.asStateFlow()
}