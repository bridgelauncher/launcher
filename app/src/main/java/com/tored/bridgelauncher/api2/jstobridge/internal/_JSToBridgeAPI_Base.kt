package com.tored.bridgelauncher.api2.jstobridge.internal

import android.app.UiModeManager
import android.app.WallpaperManager
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.util.Log
import android.webkit.WebView
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.edit
import com.tored.bridgelauncher.BridgeLauncherApplication
import com.tored.bridgelauncher.api2.jstobridge.JSToBridgeAPI
import com.tored.bridgelauncher.services.displayshape.DisplayShapeHolder
import com.tored.bridgelauncher.services.settings2.BridgeSetting
import com.tored.bridgelauncher.services.settings2.BridgeSettings
import com.tored.bridgelauncher.services.settings2.settingsDataStore
import com.tored.bridgelauncher.services.settings2.useBridgeSettingStateFlow
import com.tored.bridgelauncher.services.windowinsetsholder.WindowInsetsHolder
import com.tored.bridgelauncher.utils.showErrorToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

data class JSToBridgeAPIDeps(
    val app: BridgeLauncherApplication,
    val windowInsetsHolder: WindowInsetsHolder,
    val displayShapeHolder: DisplayShapeHolder,
)

/**
 * Base class for the [JSToBridgeAPI] inheritance chain.
 *
 * At the time of writing there was no way to split a Kotlin class into multiple files,
 * so instead there is an alphabetically-ordered chain of abstract classes, each inheriting from the previous,
 * and a concrete [JSToBridgeAPI] class at the end.
 *
 * `_JSToBridgeAPI_Base -> JSToBridgeAPI_Apps -> JSToBridgeAPI_IconPacks -> ... -> JSToBridgeAPI`
 */
@Suppress("ClassName")
abstract class _JSToBridgeAPI_Base(deps: JSToBridgeAPIDeps)
{
    protected val TAG = "JSToBridge"

    protected val _app = deps.app
    protected val _windowInsetsHolder = deps.windowInsetsHolder
    protected val _displayShapeHolder = deps.displayShapeHolder

    protected val _scope = CoroutineScope(Dispatchers.Main)

    protected val _pm = _app.packageManager
    protected val _wallman = _app.getSystemService(Context.WALLPAPER_SERVICE) as WallpaperManager
    protected val _modeman = _app.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
    protected val _dpman = _app.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

    var webView: WebView? = null
    var homeScreenContext: Context? = null


    // SETTING STATES

    protected fun <TPreference, TResult> s(setting: BridgeSetting<TPreference, TResult>) = useBridgeSettingStateFlow(_app.settingsDataStore, _scope, setting)
    protected val _isDeviceAdminEnabled = s(BridgeSettings.isDeviceAdminEnabled)
    protected val _isAccessibilityServiceEnabled = s(BridgeSettings.isAccessibilityServiceEnabled)
    protected val _theme = s(BridgeSettings.theme)
    protected val _allowProjectsToTurnScreenOff = s(BridgeSettings.allowProjectsToTurnScreenOff)
    protected val _statusBarAppearance = s(BridgeSettings.statusBarAppearance)
    protected val _navigationBarAppearance = s(BridgeSettings.navigationBarAppearance)
    protected val _showBridgeButton = s(BridgeSettings.showBridgeButton)
    protected val _drawSystemWallpaperBehindWebView = s(BridgeSettings.drawSystemWallpaperBehindWebView)
    protected val _drawWebViewOverscrollEffects = s(BridgeSettings.drawWebViewOverscrollEffects)

    protected var _lastException: Exception? = null
        set(value)
        {
            field = value.also { Log.e(TAG, "Caught exception", value) }
        }


    // region helpers

    protected fun Context.tryRun(showToastIfFailed: Boolean, f: Context.() -> Unit): Boolean
    {
        return try
        {
            f()
            true
        }
        catch (ex: Exception)
        {
            if (showToastIfFailed)
                showErrorToast(ex)

            _lastException = ex

            false
        }
    }

    protected fun tryRunInHomescreenContext(showToastIfFailed: Boolean, f: Context.() -> Unit): Boolean
    {
        return when (val context = homeScreenContext)
        {
            null -> false.also { if (showToastIfFailed) _app.showErrorToast("homeScreenContext is null") }
            else -> context.tryRun(showToastIfFailed, f)
        }
    }

    protected fun Context.tryEditPrefs(showToastIfFailed: Boolean, f: (MutablePreferences) -> Unit): Boolean
    {
        return tryRun(showToastIfFailed)
        {
            runBlocking {
                settingsDataStore.edit(f)
            }
        }
    }

    // endregion
}