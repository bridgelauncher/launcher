package com.tored.bridgelauncher.settings

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tored.bridgelauncher.SystemBarAppearanceOptions
import com.tored.bridgelauncher.ThemeOptions
import com.tored.bridgelauncher.annotations.Display
import com.tored.bridgelauncher.utils.RawRepresentable
import com.tored.bridgelauncher.utils.intToEnumOrDefault
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.KProperty1

val Context.settingsDataStore by preferencesDataStore("settings")

data class SettingsState(

    val currentProjUri: Uri? = null,
    val isQSTileAdded: Boolean = false,
    val isDeviceAdminEnabled: Boolean = false,

    val theme: ThemeOptions = ThemeOptions.System,

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

inline fun <reified TParent, TProp> getPrefKeyName(prop: KProperty1<TParent, TProp>) = getPrefKeyName(TParent::class.simpleName ?: "", prop.name)

fun getPrefKeyName(className: String, propName: String) = "${className}.${propName}"


inline fun <reified TParent, reified TEnum> Preferences.readEnum(prop: KProperty1<TParent, TEnum>, default: TEnum): TEnum
        where TEnum : Enum<TEnum>, TEnum : RawRepresentable<Int>
{
    val key = intPreferencesKey(getPrefKeyName(prop))
    return intToEnumOrDefault(this[key], default)
}

inline fun <reified TParent, reified TEnum> MutablePreferences.writeEnum(prop: KProperty1<TParent, TEnum>, value: TEnum)
        where TEnum : Enum<TEnum>, TEnum : RawRepresentable<Int>
{
    val key = intPreferencesKey(getPrefKeyName(prop))
    this[key] = value.rawValue
}


inline fun <reified TParent> Preferences.readBool(
    prop: KProperty1<TParent, Boolean>, default: Boolean
): Boolean
{
    val key = booleanPreferencesKey(getPrefKeyName(prop))
    return this[key] ?: default
}

inline fun <reified TParent> MutablePreferences.writeBool(
    prop: KProperty1<TParent, Boolean>, value: Boolean
)
{
    val key = booleanPreferencesKey(getPrefKeyName(prop))
    this[key] = value
}


inline fun <reified TParent> Preferences.readUri(
    prop: KProperty1<TParent, Uri?>
): Uri?
{
    val key = stringPreferencesKey(getPrefKeyName(prop))
    val uriStr = get(key)
    return if (uriStr == null)
        null;
    else
        Uri.parse(uriStr).also { Log.d("PREFS", "readUri: $it") }
}

inline fun <reified TParent> MutablePreferences.writeUri(
    prop: KProperty1<TParent, Uri?>, value: Uri?
)
{
    val key = stringPreferencesKey(getPrefKeyName(prop))
    if (value == null)
        remove(key)
    else
        set(key, value.toString().also { Log.d("PREFS", "writeUri: $it") })
}


@HiltViewModel
class SettingsVM @Inject constructor(@ApplicationContext appContext: Context) : ViewModel()
{
    private val _ds = appContext.settingsDataStore

    fun request()
    {
        viewModelScope.launch {
            _ds.data.collectLatest { prefs ->
                _settingsUIState.update {
                    it.copy(
                        currentProjUri = prefs.readUri(SettingsState::currentProjUri),
                        isQSTileAdded = prefs.readBool(SettingsState::isQSTileAdded, false),
                        isDeviceAdminEnabled = prefs.readBool(SettingsState::isDeviceAdminEnabled, false),

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