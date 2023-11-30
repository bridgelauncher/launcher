package com.tored.bridgelauncher.webview.jsapi

import android.annotation.SuppressLint
import android.app.UiModeManager
import android.app.WallpaperManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.captionBarIgnoringVisibility
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imeAnimationSource
import androidx.compose.foundation.layout.imeAnimationTarget
import androidx.compose.foundation.layout.mandatorySystemGestures
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsIgnoringVisibility
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsIgnoringVisibility
import androidx.compose.foundation.layout.systemGestures
import androidx.compose.foundation.layout.tappableElement
import androidx.compose.foundation.layout.tappableElementIgnoringVisibility
import androidx.compose.foundation.layout.waterfall
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.edit
import com.tored.bridgelauncher.services.BridgeLauncherDeviceAdminReceiver
import com.tored.bridgelauncher.settings.SettingsState
import com.tored.bridgelauncher.settings.SystemBarAppearanceOptions
import com.tored.bridgelauncher.settings.ThemeOptions
import com.tored.bridgelauncher.settings.settingsDataStore
import com.tored.bridgelauncher.utils.getIsSystemInNightMode
import com.tored.bridgelauncher.utils.launchApp
import com.tored.bridgelauncher.utils.messageOrDefault
import com.tored.bridgelauncher.utils.openAppInfo
import com.tored.bridgelauncher.utils.requestAppUninstall
import com.tored.bridgelauncher.utils.showErrorToast
import com.tored.bridgelauncher.utils.startBridgeAppDrawerActivity
import com.tored.bridgelauncher.utils.startBridgeSettingsActivity
import com.tored.bridgelauncher.utils.startDevConsoleActivity
import com.tored.bridgelauncher.utils.startWallpaperPickerActivity
import com.tored.bridgelauncher.utils.writeBool
import com.tored.bridgelauncher.utils.writeEnum
import com.tored.bridgelauncher.webview.WebViewState
import kotlinx.coroutines.runBlocking

private const val TAG = "JSToBridgeAPI"

data class InstalledAppInfo(
    val uid: Int,
    val packageName: String,
    val label: String,
    val labelNormalized: String,
)

typealias WindowInsetsForJS = Array<Int>

fun Density.snapshot(insets: WindowInsets): WindowInsetsForJS
{
    return arrayOf(
        insets.getLeft(this, LayoutDirection.Ltr),
        insets.getTop(this),
        insets.getRight(this, LayoutDirection.Ltr),
        insets.getBottom(this),
    )
}

fun defaultInsets() = arrayOf(0, 0, 0, 0)

class WindowInsetsSnapshot(
    val statusBars: WindowInsetsForJS = defaultInsets(),
    val statusBarsIgnoringVisibility: WindowInsetsForJS = defaultInsets(),

    val navigationBars: WindowInsetsForJS = defaultInsets(),
    val navigationBarsIgnoringVisibility: WindowInsetsForJS = defaultInsets(),

    val captionBar: WindowInsetsForJS = defaultInsets(),
    val captionBarIgnoringVisibility: WindowInsetsForJS = defaultInsets(),

    val systemBars: WindowInsetsForJS = defaultInsets(),
    val systemBarsIgnoringVisibility: WindowInsetsForJS = defaultInsets(),

    val ime: WindowInsetsForJS = defaultInsets(),
    val imeAnimationSource: WindowInsetsForJS = defaultInsets(),
    val imeAnimationTarget: WindowInsetsForJS = defaultInsets(),

    val tappableElement: WindowInsetsForJS = defaultInsets(),
    val tappableElementIgnoringVisibility: WindowInsetsForJS = defaultInsets(),

    val systemGestures: WindowInsetsForJS = defaultInsets(),
    val mandatorySystemGestures: WindowInsetsForJS = defaultInsets(),

    val displayCutout: WindowInsetsForJS = defaultInsets(),
    val waterfall: WindowInsetsForJS = defaultInsets(),
)
{
    companion object
    {
        @OptIn(ExperimentalLayoutApi::class)
        @Composable
        fun compose(): WindowInsetsSnapshot
        {
            with(LocalDensity.current)
            {
                return WindowInsetsSnapshot(
                    statusBars = snapshot(WindowInsets.statusBars),
                    statusBarsIgnoringVisibility = snapshot(WindowInsets.statusBarsIgnoringVisibility),

                    navigationBars = snapshot(WindowInsets.navigationBars),
                    navigationBarsIgnoringVisibility = snapshot(WindowInsets.navigationBarsIgnoringVisibility),

                    captionBar = snapshot(WindowInsets.captionBar),
                    captionBarIgnoringVisibility = snapshot(WindowInsets.captionBarIgnoringVisibility),

                    systemBars = snapshot(WindowInsets.systemBars),
                    systemBarsIgnoringVisibility = snapshot(WindowInsets.systemBarsIgnoringVisibility),

                    ime = snapshot(WindowInsets.ime),
                    imeAnimationSource = snapshot(WindowInsets.imeAnimationSource),
                    imeAnimationTarget = snapshot(WindowInsets.imeAnimationTarget),

                    tappableElement = snapshot(WindowInsets.tappableElement),
                    tappableElementIgnoringVisibility = snapshot(WindowInsets.tappableElementIgnoringVisibility),

                    systemGestures = snapshot(WindowInsets.systemGestures),
                    mandatorySystemGestures = snapshot(WindowInsets.mandatorySystemGestures),

                    displayCutout = snapshot(WindowInsets.displayCutout),
                    waterfall = snapshot(WindowInsets.waterfall),
                )
            }
        }
    }
}

class JSToBridgeAPI(
    private val _context: Context,
    private val _webViewState: WebViewState,
    var settingsState: SettingsState,
) : Any()
{
    private val _wallman = _context.getSystemService(Context.WALLPAPER_SERVICE) as WallpaperManager
    private val _modeman = _context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
    private val _dpman = _context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    private val _adminReceiverComponentName = ComponentName(_context, BridgeLauncherDeviceAdminReceiver::class.java)

    var windowInsetsSnapshot = WindowInsetsSnapshot()
    var displayCutoutPath: String? = null
    var displayShapePath: String? = null

    private var _lastException: Exception? = null
        set(value)
        {
            field = value.also { Log.e(TAG, "Caught exception", value) }
        }


    // region system info

    @JavascriptInterface
    fun getAndroidAPILevel() = Build.VERSION.SDK_INT

    @JavascriptInterface
    fun getLastErrorMessage() = _lastException?.messageOrDefault()

    // endregion


    // region apps

    @JavascriptInterface
    fun requestAppUninstall(packageName: String, showToastIfFailed: Boolean = true): Boolean
    {
        return _context.tryRun(showToastIfFailed) { requestAppUninstall(packageName) }
    }

    @JavascriptInterface
    fun requestOpenAppInfo(packageName: String, showToastIfFailed: Boolean = true): Boolean
    {
        return _context.tryRun(showToastIfFailed) { openAppInfo(packageName) }
    }

    @JavascriptInterface
    fun requestLaunchApp(packageName: String, showToastIfFailed: Boolean = true): Boolean
    {
        return _context.tryRun(showToastIfFailed) { launchApp(packageName) }
    }

    // endregion


    // region wallpaper

    @JavascriptInterface
    fun setWallpaperOffsetSteps(xStep: Float, yStep: Float)
    {
        _wallman.setWallpaperOffsetSteps(xStep, yStep)
    }

    @JavascriptInterface
    fun setWallpaperOffsets(x: Float, y: Float)
    {
        val token = _webViewState.webView?.applicationWindowToken

        if (token != null)
            _wallman.setWallpaperOffsets(token, x, y)
    }

    @JavascriptInterface
    fun sendWallpaperTap(x: Int, y: Int)
    {
        val token = _webViewState.webView?.applicationWindowToken
        if (token != null)
            _wallman.sendWallpaperCommand(token, WallpaperManager.COMMAND_TAP, x, y, 0, Bundle.EMPTY)
    }

    @JavascriptInterface
    fun requestChangeSystemWallpaper(showToastIfFailed: Boolean = true): Boolean
    {
        return _context.tryRun(showToastIfFailed) { startWallpaperPickerActivity() }
    }

    // endregion


    // region bridge button

    @JavascriptInterface
    fun getBridgeButtonVisibility(): String
    {
        return when (settingsState.showBridgeButton)
        {
            true -> BridgeButtonVisibility.Shown.value
            false -> BridgeButtonVisibility.Hidden.value
        }
    }

    @JavascriptInterface
    fun setBridgeButtonVisibility(state: String, showToastIfFailed: Boolean = true): Boolean
    {
        return _context.tryEditPrefs(showToastIfFailed)
        {
            it.writeBool(
                SettingsState::showBridgeButton,
                when (state)
                {
                    BridgeButtonVisibility.Shown.value -> true
                    BridgeButtonVisibility.Hidden.value -> true
                    else -> throw Exception("State must be either \"${BridgeButtonVisibility.Shown.value}\" or \"${BridgeButtonVisibility.Hidden.value}\" (got \"$state\").")
                }
            )
        }
    }

    // endregion


    // region draw system wallpaper behind webview

    @JavascriptInterface
    fun getDrawSystemWallpaperBehindWebViewEnabled(): Boolean
    {
        return settingsState.drawSystemWallpaperBehindWebView
    }

    @JavascriptInterface
    fun setDrawSystemWallpaperBehindWebViewEnabled(enable: Boolean, showToastIfFailed: Boolean = true): Boolean
    {
        return _context.tryEditPrefs(showToastIfFailed)
        {
            it.writeBool(SettingsState::drawSystemWallpaperBehindWebView, enable)
        }
    }

    // endregion


    // region system theme

    @JavascriptInterface
    fun getSystemNightMode(): String
    {
        return when (_modeman.nightMode)
        {
            UiModeManager.MODE_NIGHT_NO -> "no"
            UiModeManager.MODE_NIGHT_YES -> "yes"
            UiModeManager.MODE_NIGHT_AUTO -> "auto"
            UiModeManager.MODE_NIGHT_CUSTOM -> "custom"
            -1 -> "error"
            else -> "unknown"
        }
    }

    @JavascriptInterface
    fun resolveIsSystemInDarkTheme(): Boolean
    {
        return _context.getIsSystemInNightMode()
    }

    @JavascriptInterface
    fun setSystemNightMode(mode: String, showToastIfFailed: Boolean = true): Boolean
    {
        try
        {
            _modeman.nightMode = when (mode)
            {
                "no" -> UiModeManager.MODE_NIGHT_NO
                "yes" -> UiModeManager.MODE_NIGHT_YES
                "auto" -> UiModeManager.MODE_NIGHT_AUTO

                "custom" -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                    UiModeManager.MODE_NIGHT_CUSTOM
                else
                    throw Exception("\"custom\" requires API level 30.")

                else -> throw Exception("Mode must be one of ${q("no")}, ${q("yes")}, ${q("auto")} or, from API level 30, ${q("custom")} (got ${q(mode)}).")
            }

            return true
        }
        catch (ex: Exception)
        {
            _lastException = ex

            if (showToastIfFailed)
                _context.showErrorToast(ex.messageOrDefault())

            return false
        }
    }

    // endregion


    // region Bridge theme

    @JavascriptInterface
    fun getBridgeTheme(): String
    {
        return when (settingsState.theme)
        {
            ThemeOptions.System -> "system"
            ThemeOptions.Light -> "light"
            ThemeOptions.Dark -> "dark"
        }
    }

    @JavascriptInterface
    fun setBridgeTheme(theme: String, showToastIfFailed: Boolean = true): Boolean
    {
        return _context.tryEditPrefs(showToastIfFailed)
        {
            it.writeEnum(
                SettingsState::theme,
                when (theme)
                {
                    "system" -> ThemeOptions.System
                    "light" -> ThemeOptions.Light
                    "dark" -> ThemeOptions.Dark
                    else -> throw Exception("Theme must be one of ${q("system")}, ${q("light")} or ${q("dark")} (got ${q(theme)}).")
                }
            )
        }
    }

    // endregion


    // region system bars

    @JavascriptInterface
    fun getStatusBarAppearance(): String
    {
        return appearanceToString(settingsState.statusBarAppearance)
    }

    @JavascriptInterface
    fun setStatusBarAppearance(appearance: String, showToastIfFailed: Boolean = true): Boolean
    {
        return _context.tryEditPrefs(showToastIfFailed)
        {
            it.writeEnum(
                SettingsState::statusBarAppearance,
                stringToAppearance(appearance)
            )
        }
    }


    @JavascriptInterface
    fun getNavigationBarAppearance(): String
    {
        return appearanceToString(settingsState.navigationBarAppearance)
    }

    @JavascriptInterface
    fun setNavigationBarAppearance(appearance: String, showToastIfFailed: Boolean = true): Boolean
    {
        return _context.tryEditPrefs(showToastIfFailed)
        {
            it.writeEnum(
                SettingsState::navigationBarAppearance,
                stringToAppearance(appearance)
            )
        }
    }


    private fun appearanceToString(appearance: SystemBarAppearanceOptions): String
    {
        return when (appearance)
        {
            SystemBarAppearanceOptions.Hide -> "hide"
            SystemBarAppearanceOptions.LightIcons -> "light-fg"
            SystemBarAppearanceOptions.DarkIcons -> "dark-fg"
        }
    }

    private fun stringToAppearance(appearance: String): SystemBarAppearanceOptions
    {
        return when (appearance)
        {
            "hide" -> SystemBarAppearanceOptions.Hide
            "light-fg" -> SystemBarAppearanceOptions.LightIcons
            "dark-fg" -> SystemBarAppearanceOptions.DarkIcons
            else -> throw Exception("Appearance must be one of ${q("hide")}, ${q("light-fg")} or ${"dark-fg"} (got ${q(appearance)}).")
        }
    }

    // endregion


    // region screen locking

    @JavascriptInterface
    fun getCanLockScreen(): Boolean
    {
        return _dpman.isAdminActive(_adminReceiverComponentName)
    }

    @JavascriptInterface
    fun requestLockScreen(showToastIfFailed: Boolean = true): Boolean
    {
        try
        {
            if (_dpman.isAdminActive(_adminReceiverComponentName))
            {
                _dpman.lockNow()
                return true
            }
            else
                throw Exception("Bridge is not a device admin. Visit Bridge settings to resolve this issue.")
        }
        catch (ex: Exception)
        {
            _lastException = ex

            if (showToastIfFailed)
                _context.showErrorToast(ex.messageOrDefault())

            return false
        }
    }

    // endregion


    // region misc actions

    @JavascriptInterface
    fun requestOpenBridgeSettings(showToastIfFailed: Boolean = true): Boolean
    {
        return _context.tryRun(showToastIfFailed) { startBridgeSettingsActivity() }
    }

    @JavascriptInterface
    fun requestOpenBridgeAppDrawer(showToastIfFailed: Boolean = true): Boolean
    {
        return _context.tryRun(showToastIfFailed) { startBridgeAppDrawerActivity() }
    }

    @JavascriptInterface
    fun requestOpenDeveloperConsole(showToastIfFailed: Boolean = true): Boolean
    {
        return _context.tryRun(showToastIfFailed) { startDevConsoleActivity() }
    }

    // https://stackoverflow.com/a/15582509/6796433
    @SuppressLint("WrongConstant")
    @JavascriptInterface
    fun requestExpandNotificationShade(showToastIfFailed: Boolean = true): Boolean
    {
        try
        {
            val sbservice: Any = _context.getSystemService("statusbar")
            val statusbarManager = Class.forName("android.app.StatusBarManager")
            val showsb = statusbarManager.getMethod("expandNotificationsPanel")
            showsb.invoke(sbservice)

            return true
        }
        catch (ex: Exception)
        {
            _lastException = ex

            if (showToastIfFailed)
                _context.showErrorToast(ex.messageOrDefault())

            return false
        }
    }

    // endregion


    // region toast

    @JavascriptInterface
    fun showToast(message: String, long: Boolean = false)
    {
        Toast.makeText(_context, message, if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
    }

    // endregion


    // region window insets & cutouts

    @JavascriptInterface
    fun getStatusBarsWindowInsets() = windowInsetsSnapshot.statusBars

    @JavascriptInterface
    fun getStatusBarsIgnoringVisibilityWindowInsets() = windowInsetsSnapshot.statusBarsIgnoringVisibility


    @JavascriptInterface
    fun getNavigationBarsWindowInsets() = windowInsetsSnapshot.navigationBars

    @JavascriptInterface
    fun getNavigationBarsIgnoringVisibilityWindowInsets() = windowInsetsSnapshot.navigationBarsIgnoringVisibility


    @JavascriptInterface
    fun getCaptionBarWindowInsets() = windowInsetsSnapshot.captionBar

    @JavascriptInterface
    fun getCaptionBarIgnoringVisibilityWindowInsets() = windowInsetsSnapshot.captionBarIgnoringVisibility


    @JavascriptInterface
    fun getSystemBarsWindowInsets() = windowInsetsSnapshot.systemBars

    @JavascriptInterface
    fun getSystemBarsIgnoringVisibilityWindowInsets() = windowInsetsSnapshot.systemBarsIgnoringVisibility


    @JavascriptInterface
    fun getImeWindowInsets() = windowInsetsSnapshot.ime

    @JavascriptInterface
    fun getImeAnimationSourceWindowInsets() = windowInsetsSnapshot.imeAnimationSource

    @JavascriptInterface
    fun getImeAnimationTargetWindowInsets() = windowInsetsSnapshot.imeAnimationTarget


    @JavascriptInterface
    fun getTappableElementWindowInsets() = windowInsetsSnapshot.tappableElement

    @JavascriptInterface
    fun getTappableElementIgnoringVisibilityWindowInsets() = windowInsetsSnapshot.tappableElementIgnoringVisibility


    @JavascriptInterface
    fun getSystemGesturesWindowInsets() = windowInsetsSnapshot.systemGestures

    @JavascriptInterface
    fun getMandatorySystemGesturesWindowInsets() = windowInsetsSnapshot.mandatorySystemGestures


    @JavascriptInterface
    fun getDisplayCutoutWindowInsets() = windowInsetsSnapshot.displayCutout

    @JavascriptInterface
    fun getWaterfallWindowInsets() = windowInsetsSnapshot.waterfall


    @JavascriptInterface
    fun getDisplayCutoutPath() = displayCutoutPath

    @JavascriptInterface
    fun getDisplayShapePath() = displayCutoutPath

    // endregion


    // region helpers

    private fun Context.tryRun(showToastIfFailed: Boolean, f: Context.() -> Unit): Boolean
    {
        return try
        {
            f()
            true
        }
        catch (ex: Exception)
        {
            if (showToastIfFailed)
                showErrorToast(ex.messageOrDefault())

            _lastException = ex

            false
        }
    }

    private fun Context.tryEditPrefs(showToastIfFailed: Boolean, f: (MutablePreferences) -> Unit): Boolean
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


enum class BridgeButtonVisibility(val value: String)
{
    Shown("shown"),
    Hidden("hidden"),
}

private fun q(s: String) = "\"$s\""