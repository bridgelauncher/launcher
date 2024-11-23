package com.tored.bridgelauncher.api.jsapi

import android.Manifest
import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.app.UiModeManager
import android.app.WallpaperManager
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.edit
import com.tored.bridgelauncher.BridgeLauncherApplication
import com.tored.bridgelauncher.api.server.BridgeServer
import com.tored.bridgelauncher.api.server.endpoints.AppIconsEndpoint
import com.tored.bridgelauncher.api.server.endpoints.IconPackContentEndpoint
import com.tored.bridgelauncher.api.server.endpoints.IconPacksEndpoint
import com.tored.bridgelauncher.api.server.getBridgeApiEndpointURL
import com.tored.bridgelauncher.services.settings.ThemeOptions
import com.tored.bridgelauncher.services.settings.settingsDataStore
import com.tored.bridgelauncher.services.settings2.BridgeSetting
import com.tored.bridgelauncher.services.settings2.BridgeSettings
import com.tored.bridgelauncher.services.settings2.getIsBridgeAbleToLockTheScreen
import com.tored.bridgelauncher.services.settings2.setBridgeSetting
import com.tored.bridgelauncher.services.settings2.useBridgeSettingStateFlow
import com.tored.bridgelauncher.services.system.BridgeLauncherAccessibilityService
import com.tored.bridgelauncher.utils.CurrentAndroidVersion
import com.tored.bridgelauncher.utils.getIsSystemInNightMode
import com.tored.bridgelauncher.utils.launchApp
import com.tored.bridgelauncher.utils.messageOrDefault
import com.tored.bridgelauncher.utils.openAppInfo
import com.tored.bridgelauncher.utils.q
import com.tored.bridgelauncher.utils.requestAppUninstall
import com.tored.bridgelauncher.utils.showErrorToast
import com.tored.bridgelauncher.utils.startBridgeAppDrawerActivity
import com.tored.bridgelauncher.utils.startBridgeSettingsActivity
import com.tored.bridgelauncher.utils.startDevConsoleActivity
import com.tored.bridgelauncher.utils.startWallpaperPickerActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

private const val TAG = "JSToBridgeAPI"


class JSToBridgeAPI(
    private val _app: BridgeLauncherApplication,
    var webView: WebView?,
) : Any()
{
    private val _scope = CoroutineScope(Dispatchers.Main)

    private val _wallman = _app.getSystemService(Context.WALLPAPER_SERVICE) as WallpaperManager
    private val _modeman = _app.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
    private val _dpman = _app.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

    var windowInsetsSnapshot = WindowInsetsSnapshots()
    var displayCutoutPathSnapshot: String? = null
    var displayShapePathSnapshot: String? = null

    // SETTING STATES

    private fun <TPreference, TResult> s(setting: BridgeSetting<TPreference, TResult>) = useBridgeSettingStateFlow(_app.settingsDataStore, _scope, setting)
    private val _isDeviceAdminEnabled = s(BridgeSettings.isDeviceAdminEnabled)
    private val _isAccessibilityServiceEnabled = s(BridgeSettings.isAccessibilityServiceEnabled)
    private val _theme = s(BridgeSettings.theme)
    private val _allowProjectsToTurnScreenOff = s(BridgeSettings.allowProjectsToTurnScreenOff)
    private val _statusBarAppearance = s(BridgeSettings.statusBarAppearance)
    private val _navigationBarAppearance = s(BridgeSettings.navigationBarAppearance)
    private val _showBridgeButton = s(BridgeSettings.showBridgeButton)
    private val _drawSystemWallpaperBehindWebView = s(BridgeSettings.drawSystemWallpaperBehindWebView)
    private val _drawWebViewOverscrollEffects = s(BridgeSettings.drawWebViewOverscrollEffects)

    private var _lastException: Exception? = null
        set(value)
        {
            field = value.also { Log.e(TAG, "Caught exception", value) }
        }


    // region system

    @JavascriptInterface
    fun getAndroidAPILevel() = Build.VERSION.SDK_INT


    @JavascriptInterface
    fun getBridgeVersionCode() = _app.packageManager
        .getPackageInfo(_app.packageName, 0)
        .run {
            if (CurrentAndroidVersion.supportsPackageInfoLongVersionCode())
                longVersionCode
            else
                @Suppress("DEPRECATION")
                versionCode.toLong()
        }

    @JavascriptInterface
    fun getBridgeVersionName(): String = _app.packageManager.getPackageInfo(_app.packageName, 0).versionName ?: ""


    @JavascriptInterface
    fun getLastErrorMessage() = _lastException?.messageOrDefault()

    // endregion


    // region fetch

    @JavascriptInterface
    fun getProjectURL() = BridgeServer.PROJECT_URL

    @JavascriptInterface
    fun getAppsURL() = getBridgeApiEndpointURL(BridgeServer.ENDPOINT_APPS)

    // endregion


    // region apps

    @JvmOverloads
    @JavascriptInterface
    fun requestAppUninstall(packageName: String, showToastIfFailed: Boolean = true): Boolean
    {
        return _app.tryRun(showToastIfFailed) { requestAppUninstall(packageName) }
    }

    @JvmOverloads
    @JavascriptInterface
    fun requestOpenAppInfo(packageName: String, showToastIfFailed: Boolean = true): Boolean
    {
        return _app.tryRun(showToastIfFailed) { openAppInfo(packageName) }
    }

    @JvmOverloads
    @JavascriptInterface
    fun requestLaunchApp(packageName: String, showToastIfFailed: Boolean = true): Boolean
    {
        return _app.tryRun(showToastIfFailed) { launchApp(packageName) }
    }

    // endregion


    // region icon packs

    @JavascriptInterface
    fun getIconPacksURL(includeItems: Boolean = false): String =
        getBridgeApiEndpointURL(
            BridgeServer.ENDPOINT_ICON_PACKS,
            IconPacksEndpoint.QUERY_INCLUDE_ITEMS to includeItems,
        )

    @JavascriptInterface
    fun getIconPackInfoURL(iconPackPackageName: String, includeItems: Boolean = false) =
        getBridgeApiEndpointURL(
            BridgeServer.ENDPOINT_ICON_PACKS,
            IconPacksEndpoint.QUERY_ICON_PACK_PACKAGE_NAME to iconPackPackageName,
            IconPacksEndpoint.QUERY_INCLUDE_ITEMS to includeItems,
        )

    @JavascriptInterface
    fun getIconPackAppFilterXMLURL(iconPackPackageName: String, includeItems: Boolean = false) =
        getBridgeApiEndpointURL(
            BridgeServer.ENDPOINT_ICON_PACKS,
            IconPacksEndpoint.QUERY_ICON_PACK_PACKAGE_NAME to iconPackPackageName,
            IconPacksEndpoint.QUERY_INCLUDE_ITEMS to includeItems,
        )

    // endregion


    // region icons

    @JavascriptInterface
    fun getDefaultAppIconURL(packageName: String) =
        getBridgeApiEndpointURL(
            BridgeServer.ENDPOINT_APP_ICONS,
            AppIconsEndpoint.QUERY_PACKAGE_NAME to packageName,
        )

    @JvmOverloads
    @JavascriptInterface
    fun getAppIconURL(appPackageName: String, iconPackPackageName: String? = null) =
        getBridgeApiEndpointURL(
            BridgeServer.ENDPOINT_APP_ICONS,
            AppIconsEndpoint.QUERY_PACKAGE_NAME to appPackageName,
            AppIconsEndpoint.QUERY_ICON_PACK_PACKAGE_NAME to iconPackPackageName,
            AppIconsEndpoint.QUERY_NOT_FOUND_BEHAVIOR to AppIconsEndpoint.IconNotFoundBehaviors.Default,
        )

    @JavascriptInterface
    fun getIconPackAppIconURL(iconPackPackageName: String, appPackageName: String) =
        getBridgeApiEndpointURL(
            BridgeServer.ENDPOINT_APP_ICONS,
            AppIconsEndpoint.QUERY_PACKAGE_NAME to appPackageName,
            AppIconsEndpoint.QUERY_ICON_PACK_PACKAGE_NAME to iconPackPackageName,
            AppIconsEndpoint.QUERY_NOT_FOUND_BEHAVIOR to AppIconsEndpoint.IconNotFoundBehaviors.Error,
        )

    @JavascriptInterface
    fun getIconPackAppItemURL(iconPackPackageName: String, itemName: String) =
        getBridgeApiEndpointURL(
            BridgeServer.ENDPOINT_ICON_PACK_CONTENT,
            IconPackContentEndpoint.QUERY_ICON_PACK_PACKAGE_NAME to iconPackPackageName,
            IconPackContentEndpoint.QUERY_ITEM_NAME to itemName,
        )


    @JavascriptInterface
    fun getIconPackDrawableURL(iconPackPackageName: String, drawableName: String) =
        getBridgeApiEndpointURL(
            BridgeServer.ENDPOINT_ICON_PACK_CONTENT,
            IconPackContentEndpoint.QUERY_ICON_PACK_PACKAGE_NAME to iconPackPackageName,
            IconPackContentEndpoint.QUERY_DRAWABLE_NAME to drawableName,
        )

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
        val token = webView?.applicationWindowToken

        if (token != null)
        {
            _wallman.setWallpaperOffsets(token, x, y)
        }
    }

    @JvmOverloads
    @JavascriptInterface
    fun sendWallpaperTap(x: Float, y: Float, z: Float = 0f)
    {
        val token = webView?.applicationWindowToken
        if (token != null)
        {
            val metrics = _app.resources.displayMetrics
            _wallman.sendWallpaperCommand(
                token,
                WallpaperManager.COMMAND_TAP,
                metrics.toPx(x).toInt(),
                metrics.toPx(y).toInt(),
                metrics.toPx(z).toInt(),
                Bundle.EMPTY
            )
        }
    }

    @JvmOverloads
    @JavascriptInterface
    fun requestChangeSystemWallpaper(showToastIfFailed: Boolean = true): Boolean
    {
        return _app.tryRun(showToastIfFailed) { startWallpaperPickerActivity() }
    }

    // endregion


    // region bridge button

    @JavascriptInterface
    fun getBridgeButtonVisibility(): String
    {
        return getBridgeButtonVisiblityString(_showBridgeButton.value)
    }

    @JvmOverloads
    @JavascriptInterface
    fun requestSetBridgeButtonVisibility(state: String, showToastIfFailed: Boolean = true): Boolean
    {
        return _app.tryEditPrefs(showToastIfFailed)
        {
            it.setBridgeSetting(
                BridgeSettings.showBridgeButton,
                when (state)
                {
                    BridgeButtonVisibility.Shown.value -> true
                    BridgeButtonVisibility.Hidden.value -> false
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
        return _drawSystemWallpaperBehindWebView.value
    }

    @JvmOverloads
    @JavascriptInterface
    fun requestSetDrawSystemWallpaperBehindWebViewEnabled(enable: Boolean, showToastIfFailed: Boolean = true): Boolean
    {
        return _app.tryEditPrefs(showToastIfFailed)
        {
            it.setBridgeSetting(BridgeSettings.drawSystemWallpaperBehindWebView, enable)
        }
    }

    // endregion


    // region overscroll effects

    @JavascriptInterface
    fun getOverscrollEffects(): String
    {
        return getOverscrollEffects(_drawWebViewOverscrollEffects.value)
    }

    @JvmOverloads
    @JavascriptInterface
    fun requestSetOverscrollEffects(effects: String, showToastIfFailed: Boolean = true): Boolean
    {
        return _app.tryEditPrefs(showToastIfFailed)
        {
            it.setBridgeSetting(
                BridgeSettings.drawWebViewOverscrollEffects,
                when (effects)
                {
                    OverscrollEffects.Default.value -> true
                    OverscrollEffects.None.value -> false
                    else -> throw Exception("Effects must be either \"${OverscrollEffects.Default.value}\" or \"${OverscrollEffects.None.value}\" (got \"$effects\").")
                }
            )
        }
    }

    // endregion


    // region system night mode

    @JavascriptInterface
    fun getSystemNightMode(): String
    {
        return getSystemNightModeString(_modeman.nightMode)
    }

    @JavascriptInterface
    fun resolveIsSystemInDarkTheme(): Boolean
    {
        return _app.getIsSystemInNightMode()
    }

    @JavascriptInterface
    fun getCanSetSystemNightMode(): Boolean
    {
        return ActivityCompat.checkSelfPermission(_app, "android.permission.MODIFY_DAY_NIGHT_MODE") == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(_app, Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("WrongConstant")
    @JvmOverloads
    @JavascriptInterface
    fun requestSetSystemNightMode(mode: String, showToastIfFailed: Boolean = true): Boolean
    {
        return _app.tryRun(showToastIfFailed)
        {
            Log.d(TAG, "requestSetSystemNightMode: $mode")

            val modeInt = when (mode)
            {
                "no" -> UiModeManager.MODE_NIGHT_NO
                "yes" -> UiModeManager.MODE_NIGHT_YES
                "auto" -> UiModeManager.MODE_NIGHT_AUTO

                "custom" -> if (CurrentAndroidVersion.supportsNightModeCustom())
                    UiModeManager.MODE_NIGHT_CUSTOM
                else
                    throw Exception("\"custom\" requires API level 30 (Android 11).")

                else -> throw Exception("Mode must be one of ${q("no")}, ${q("yes")}, ${q("auto")} or, from API level 30 (Android 11), ${q("custom")} (got ${q(mode)}).")
            }

            val hasModifyPerm = ActivityCompat.checkSelfPermission(_app, "android.permission.MODIFY_DAY_NIGHT_MODE") == PackageManager.PERMISSION_GRANTED

            if (hasModifyPerm)
            {
                _modeman.nightMode = modeInt
            }
            else
            {
                val hasWriteSecureSettingsPerm = ActivityCompat.checkSelfPermission(_app, Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED

                if (hasWriteSecureSettingsPerm)
                {
                    // shoutouts to joaomgcd (Tasker dev) for this workaround!
                    Settings.Secure.putInt(_app.contentResolver, "ui_night_mode", modeInt)
                    _modeman.enableCarMode(UiModeManager.ENABLE_CAR_MODE_ALLOW_SLEEP)
                    _modeman.disableCarMode(0)
                }
                else
                {
                    Toast
                        .makeText(
                            _app,
                            "To set system night mode, Bridge needs the WRITE_SECURE_SETTINGS permission, which can be granted via ADB. "
                                    + "Check the documentation for more information.",
                            Toast.LENGTH_LONG
                        )
                        .show()
                }
            }
        }
    }

    // endregion


    // region Bridge theme

    @JavascriptInterface
    fun getBridgeTheme(): String
    {
        return getBridgeThemeString(_theme.value)
    }

    @JvmOverloads
    @JavascriptInterface
    fun requestSetBridgeTheme(theme: String, showToastIfFailed: Boolean = true): Boolean
    {
        return _app.tryEditPrefs(showToastIfFailed)
        {
            it.setBridgeSetting(
                BridgeSettings.theme,
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
        return getSystemBarAppearanceString(_statusBarAppearance.value)
    }

    @JvmOverloads
    @JavascriptInterface
    fun requestSetStatusBarAppearance(appearance: String, showToastIfFailed: Boolean = true): Boolean
    {
        return _app.tryEditPrefs(showToastIfFailed)
        {
            it.setBridgeSetting(
                BridgeSettings.statusBarAppearance,
                stringToSystemBarAppearance(appearance)
            )
        }
    }


    @JavascriptInterface
    fun getNavigationBarAppearance(): String
    {
        return getSystemBarAppearanceString(_navigationBarAppearance.value)
    }

    @JvmOverloads
    @JavascriptInterface
    fun requestSetNavigationBarAppearance(appearance: String, showToastIfFailed: Boolean = true): Boolean
    {
        return _app.tryEditPrefs(showToastIfFailed)
        {
            it.setBridgeSetting(
                BridgeSettings.navigationBarAppearance,
                stringToSystemBarAppearance(appearance)
            )
        }
    }

    // endregion


    // region screen locking

    @JavascriptInterface
    fun getCanLockScreen(): Boolean
    {
        return getIsBridgeAbleToLockTheScreen(
            isAccessibilityServiceEnabled = _isAccessibilityServiceEnabled.value,
            isDeviceAdminEnabled = _isDeviceAdminEnabled.value,
            allowProjectsToTurnScreenOff = _allowProjectsToTurnScreenOff.value,
        )
    }

    @JvmOverloads
    @JavascriptInterface
    fun requestLockScreen(showToastIfFailed: Boolean = true): Boolean
    {
        return _app.tryRun(showToastIfFailed)
        {
            if (!CurrentAndroidVersion.supportsAccessiblityServiceScreenLock() && !_isDeviceAdminEnabled.value)
            {
                throw Exception("Bridge is not a device admin. Visit Bridge Settings to resolve the issue.")
            }
            else if (CurrentAndroidVersion.supportsAccessiblityServiceScreenLock() && !_isAccessibilityServiceEnabled.value)
            {
                throw Exception("Bridge Accessibility Service is not enabled. Visit Bridge Settings to resolve the issue.")
            }

            if (!_allowProjectsToTurnScreenOff.value)
            {
                throw Exception("Projects are not allowed to lock the screen. Visit Bridge Settings to resolve the issue.")
            }

            if (CurrentAndroidVersion.supportsAccessiblityServiceScreenLock())
            {
                if (BridgeLauncherAccessibilityService.instance == null)
                {
                    throw Exception("Cannot access the Bridge Accessibility Service instance. This is a bug.")
                }
                else
                {
                    BridgeLauncherAccessibilityService.instance?.performGlobalAction(AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN)
                }
            }
            else
            {
                _dpman.lockNow()
            }
        }
    }

    // endregion


    // region misc actions

    @JvmOverloads
    @JavascriptInterface
    fun requestOpenBridgeSettings(showToastIfFailed: Boolean = true): Boolean
    {
        return _app.tryRun(showToastIfFailed) { startBridgeSettingsActivity() }
    }

    @JvmOverloads
    @JavascriptInterface
    fun requestOpenBridgeAppDrawer(showToastIfFailed: Boolean = true): Boolean
    {
        return _app.tryRun(showToastIfFailed) { startBridgeAppDrawerActivity() }
    }

    @JvmOverloads
    @JavascriptInterface
    fun requestOpenDeveloperConsole(showToastIfFailed: Boolean = true): Boolean
    {
        return _app.tryRun(showToastIfFailed) { startDevConsoleActivity() }
    }

    // https://stackoverflow.com/a/15582509/6796433
    @JvmOverloads
    @SuppressLint("WrongConstant")
    @JavascriptInterface
    fun requestExpandNotificationShade(showToastIfFailed: Boolean = true): Boolean
    {
        try
        {
            val sbservice: Any = _app.getSystemService("statusbar")
            val statusbarManager = Class.forName("android.app.StatusBarManager")
            val showsb = statusbarManager.getMethod("expandNotificationsPanel")
            showsb.invoke(sbservice)

            return true
        }
        catch (ex: Exception)
        {
            _lastException = ex

            if (showToastIfFailed)
                _app.showErrorToast(ex)

            return false
        }
    }

    // endregion


    // region toast

    @JvmOverloads
    @JavascriptInterface
    fun showToast(message: String, long: Boolean = false)
    {
        Toast.makeText(_app, message, if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
    }

    // endregion


    // region window insets & cutouts

    @JavascriptInterface
    fun getStatusBarsWindowInsets() = windowInsetsSnapshot.statusBars.toJson()

    @JavascriptInterface
    fun getStatusBarsIgnoringVisibilityWindowInsets() = windowInsetsSnapshot.statusBarsIgnoringVisibility.toJson()


    @JavascriptInterface
    fun getNavigationBarsWindowInsets() = windowInsetsSnapshot.navigationBars.toJson()

    @JavascriptInterface
    fun getNavigationBarsIgnoringVisibilityWindowInsets() = windowInsetsSnapshot.navigationBarsIgnoringVisibility.toJson()


    @JavascriptInterface
    fun getCaptionBarWindowInsets() = windowInsetsSnapshot.captionBar.toJson()

    @JavascriptInterface
    fun getCaptionBarIgnoringVisibilityWindowInsets() = windowInsetsSnapshot.captionBarIgnoringVisibility.toJson()


    @JavascriptInterface
    fun getSystemBarsWindowInsets() = windowInsetsSnapshot.systemBars.toJson()

    @JavascriptInterface
    fun getSystemBarsIgnoringVisibilityWindowInsets() = windowInsetsSnapshot.systemBarsIgnoringVisibility.toJson()


    @JavascriptInterface
    fun getImeWindowInsets() = windowInsetsSnapshot.ime.toJson()

    @JavascriptInterface
    fun getImeAnimationSourceWindowInsets() = windowInsetsSnapshot.imeAnimationSource.toJson()

    @JavascriptInterface
    fun getImeAnimationTargetWindowInsets() = windowInsetsSnapshot.imeAnimationTarget.toJson()


    @JavascriptInterface
    fun getTappableElementWindowInsets() = windowInsetsSnapshot.tappableElement.toJson()

    @JavascriptInterface
    fun getTappableElementIgnoringVisibilityWindowInsets() = windowInsetsSnapshot.tappableElementIgnoringVisibility.toJson()


    @JavascriptInterface
    fun getSystemGesturesWindowInsets() = windowInsetsSnapshot.systemGestures.toJson()

    @JavascriptInterface
    fun getMandatorySystemGesturesWindowInsets() = windowInsetsSnapshot.mandatorySystemGestures.toJson()


    @JavascriptInterface
    fun getDisplayCutoutWindowInsets() = windowInsetsSnapshot.displayCutout.toJson()

    @JavascriptInterface
    fun getWaterfallWindowInsets() = windowInsetsSnapshot.waterfall.toJson()


    @JavascriptInterface
    fun getDisplayCutoutPath() = displayCutoutPathSnapshot

    @JavascriptInterface
    fun getDisplayShapePath() = displayCutoutPathSnapshot

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
                showErrorToast(ex)

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

private fun DisplayMetrics.toPx(x: Float) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, x, this)
