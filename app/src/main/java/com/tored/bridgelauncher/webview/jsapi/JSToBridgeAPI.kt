package com.tored.bridgelauncher.webview.jsapi

import android.annotation.SuppressLint
import android.app.UiModeManager
import android.app.WallpaperManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.edit
import com.tored.bridgelauncher.services.BridgeLauncherDeviceAdminReceiver
import com.tored.bridgelauncher.settings.SettingsState
import com.tored.bridgelauncher.settings.SystemBarAppearanceOptions
import com.tored.bridgelauncher.settings.ThemeOptions
import com.tored.bridgelauncher.settings.settingsDataStore
import com.tored.bridgelauncher.utils.getIsSystemInNightMode
import com.tored.bridgelauncher.utils.messageOrDefault
import com.tored.bridgelauncher.utils.showErrorToast
import com.tored.bridgelauncher.utils.startAppDrawerActivity
import com.tored.bridgelauncher.utils.startBridgeSettingsActivity
import com.tored.bridgelauncher.utils.startDevConsoleActivity
import com.tored.bridgelauncher.utils.startWallpaperPickerActivity
import com.tored.bridgelauncher.utils.tryLaunchApp
import com.tored.bridgelauncher.utils.tryOpenAppInfo
import com.tored.bridgelauncher.utils.tryRequestAppUninstall
import com.tored.bridgelauncher.utils.writeBool
import com.tored.bridgelauncher.utils.writeEnum
import com.tored.bridgelauncher.webview.WebViewState
import kotlinx.coroutines.runBlocking


data class InstalledAppInfo(
    val uid: Int,
    val packageName: String,
    val label: String,
    val labelNormalized: String,
)

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

    // region apps

    @JavascriptInterface
    fun requestAppUninstall(packageName: String, showToastIfFailed: Boolean = true): Boolean
    {
        return _context.tryRequestAppUninstall(packageName, showToastIfFailed)
    }

    @JavascriptInterface
    fun openAppInfo(packageName: String, showToastIfFailed: Boolean = true): Boolean
    {
        return _context.tryOpenAppInfo(packageName, showToastIfFailed)
    }

    @JavascriptInterface
    fun launchApp(packageName: String, showToastIfFailed: Boolean = true): Boolean
    {
        return _context.tryLaunchApp(packageName, showToastIfFailed)
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
    fun requestChangeSystemWallpaper()
    {
        _context.startWallpaperPickerActivity()
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
    fun setBridgeButtonVisibility(state: String, showToastIfFailed: Boolean = true): String
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
    fun setDrawSystemWallpaperBehindWebViewEnabled(enable: Boolean, showToastIfFailed: Boolean = true): String
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
    fun setSystemNightMode(mode: String, showToastIfFailed: Boolean = true): String
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

            return ""
        }
        catch (ex: Exception)
        {
            return ex.messageOrDefault().also {
                if (showToastIfFailed)
                    _context.showErrorToast(it)
            }
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
    fun setBridgeTheme(theme: String, showToastIfFailed: Boolean = true): String
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
    fun setStatusBarAppearance(appearance: String, showToastIfFailed: Boolean = true): String
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
    fun setNavigationBarAppearance(appearance: String, showToastIfFailed: Boolean = true): String
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
    fun requestLockScreen(showToastIfFailed: Boolean = true): String
    {
        try
        {
            if (_dpman.isAdminActive(_adminReceiverComponentName))
            {
                _dpman.lockNow()
                return ""
            }
            else
                throw Exception("Bridge is not a device admin. Visit Bridge settings to resolve this issue.")
        }
        catch (ex: Exception)
        {
            return ex.messageOrDefault().also {
                if (showToastIfFailed)
                    _context.showErrorToast(it)
            }
        }
    }

    // endregion


    // region misc actions

    @JavascriptInterface
    fun requestOpenBridgeSettings()
    {
        _context.startBridgeSettingsActivity()
    }

    @JavascriptInterface
    fun requestOpenAppDrawer()
    {
        _context.startAppDrawerActivity()
    }

    @JavascriptInterface
    fun requestOpenDeveloperConsole()
    {
        _context.startDevConsoleActivity()
    }

    // https://stackoverflow.com/a/15582509/6796433
    @SuppressLint("WrongConstant")
    @JavascriptInterface
    fun requestExpandNotificationShade(showToastIfFailed: Boolean = true): String
    {
        return try
        {
            val sbservice: Any = _context.getSystemService("statusbar")
            val statusbarManager = Class.forName("android.app.StatusBarManager")
            val showsb = statusbarManager.getMethod("expandNotificationsPanel")
            showsb.invoke(sbservice)

            ""
        }
        catch (ex: Exception)
        {
            return ex.messageOrDefault().also {
                if (showToastIfFailed)
                    _context.showErrorToast(it)
            }
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


    // region helpers

    private fun Context.tryEditPrefs(showToastIfFailed: Boolean, f: (MutablePreferences) -> Unit): String
    {
        return try
        {
            runBlocking {
                settingsDataStore.edit(f)
            }

            ""
        }
        catch (ex: Exception)
        {
            ex.messageOrDefault().also {
                if (showToastIfFailed)
                    showErrorToast(it)
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