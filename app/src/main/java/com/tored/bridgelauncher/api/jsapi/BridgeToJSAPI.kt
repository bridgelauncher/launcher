package com.tored.bridgelauncher.api.jsapi

import android.content.Context
import android.util.Log
import android.webkit.WebView
import com.tored.bridgelauncher.services.apps.InstalledAppsHolder
import com.tored.bridgelauncher.services.apps.SerializableInstalledApp
import com.tored.bridgelauncher.services.settings.ISettingsStateProvider
import com.tored.bridgelauncher.services.settings.SettingsState
import com.tored.bridgelauncher.services.settings.getCanLockScreen
import com.tored.bridgelauncher.utils.checkCanSetSystemNightMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

private const val TAG = "BridgeToJSAPI"

interface BridgeEventArgs
{
}

@Serializable
data class AppRemovedEventArgs(val packageName: String) : BridgeEventArgs

@Serializable
data class ValueChangeEventArgs<T>(val newValue: T) : BridgeEventArgs

class BridgeToJSAPI(
    private val _context: Context,
    private val _coroutineScope: CoroutineScope,
    private val _settings: ISettingsStateProvider,
    private val _installedApps: InstalledAppsHolder,
    private var _lastCanSetSystemNightMode: Boolean,
)
{
    init
    {
        // subscribe to app list change events
        _installedApps.onAdded { raiseAppInstalled(it.toSerializable()) }
        _installedApps.onChanged { raiseAppChanged(it.toSerializable()) }
        _installedApps.onRemoved { raiseAppRemoved(it) }

        _coroutineScope.launch()
        {
            var prev: SettingsState? = null
            _settings.settingsState.collectLatest()
            { new ->
                val old = prev

                if (old != null)
                {
                    if (new.showBridgeButton != old.showBridgeButton)
                        raiseBridgeButtonVisibilityChanged(getBridgeButtonVisiblityString(new.showBridgeButton))

                    if (new.drawSystemWallpaperBehindWebView != old.drawSystemWallpaperBehindWebView)
                        raiseDrawSystemWallpaperBehindWebViewChanged(new.drawSystemWallpaperBehindWebView)

                    if (new.drawWebViewOverscrollEffects != old.drawWebViewOverscrollEffects)
                        raiseOverscrollEffectsChanged(getOverscrollEffects(new.drawWebViewOverscrollEffects))

                    if (new.theme != old.theme)
                        raiseBridgeThemeChanged(getBridgeThemeString(new.theme))

                    if (new.statusBarAppearance != old.statusBarAppearance)
                        raiseStatusBarAppearanceChanged(getSystemBarAppearanceString(new.statusBarAppearance))

                    if (new.navigationBarAppearance != old.navigationBarAppearance)
                        raiseNavigationBarAppearanceChanged(getSystemBarAppearanceString(new.navigationBarAppearance))

                    if (new.getCanLockScreen() != old.getCanLockScreen())
                        raiseCanLockScreenChanged(new.getCanLockScreen())
                }

                prev = new
            }
        }
    }

    private var _prevWindowInsetsSnapshot: WindowInsetsSnapshots? = null

    var webView: WebView? = null

    fun raiseAppInstalled(app: SerializableInstalledApp) = notify("appInstalled", app)
    fun raiseAppChanged(app: SerializableInstalledApp) = notify("appChanged", app)
    fun raiseAppRemoved(packageName: String) = notify("appRemoved", AppRemovedEventArgs(packageName))

    fun raiseBeforePause() = notify("beforePause")
    fun raiseAfterResume() = notify("afterResume")

    fun raiseBridgeButtonVisibilityChanged(newValue: String) = notify("bridgeButtonVisibilityChanged", newValue)
    fun raiseDrawSystemWallpaperBehindWebViewChanged(newValue: Boolean) = notify("drawSystemWallpaperBehindWebViewChanged", newValue)
    fun raiseOverscrollEffectsChanged(newValue: String) = notify("overscrollEffectsChanged", newValue)
    fun raiseBridgeThemeChanged(newValue: String) = notify("bridgeThemeChanged", newValue)
    fun raiseStatusBarAppearanceChanged(newValue: String) = notify("statusBarAppearanceChanged", newValue)
    fun raiseNavigationBarAppearanceChanged(newValue: String) = notify("navigationBarAppearanceChanged", newValue)

    // intended to be called from onResume() - there is no API to listen for permission changes, so checks in onResume it is
    fun notifyCanSetSystemNightModeMightHaveChanged()
    {
        val canSetSystemNightMode = _context.checkCanSetSystemNightMode()
        if (canSetSystemNightMode != _lastCanSetSystemNightMode)
        {
            raiseCanSetSystemNightModeChanged(canSetSystemNightMode)
            _lastCanSetSystemNightMode = canSetSystemNightMode
        }
    }

    fun raiseCanSetSystemNightModeChanged(newValue: Boolean) = notify("canSetSystemNightModeChanged", newValue)

    fun raiseCanLockScreenChanged(newValue: Boolean) = notify("canLockScreenChanged", newValue)

    fun notifySystemNightModeChanged(newValue: String) = notify("systemNightModeChanged", newValue)

    fun windowInsetsSnapshotsChanged(new: WindowInsetsSnapshots)
    {
        val old = _prevWindowInsetsSnapshot

        if (old != null)
        {
            raiseIfChanged("statusBars", old.statusBars, new.statusBars)
            raiseIfChanged("statusBarsIgnoringVisibility", old.statusBarsIgnoringVisibility, new.statusBarsIgnoringVisibility)

            raiseIfChanged("navigationBars", old.navigationBars, new.navigationBars)
            raiseIfChanged("navigationBarsIgnoringVisibility", old.navigationBarsIgnoringVisibility, new.navigationBarsIgnoringVisibility)

            raiseIfChanged("captionBar", old.captionBar, new.captionBar)
            raiseIfChanged("captionBarIgnoringVisibility", old.captionBarIgnoringVisibility, new.captionBarIgnoringVisibility)

            raiseIfChanged("systemBars", old.systemBars, new.systemBars)
            raiseIfChanged("systemBarsIgnoringVisibility", old.systemBarsIgnoringVisibility, new.systemBarsIgnoringVisibility)

            raiseIfChanged("ime", old.ime, new.ime)
            raiseIfChanged("imeAnimationSource", old.imeAnimationSource, new.imeAnimationSource)
            raiseIfChanged("imeAnimationTarget", old.imeAnimationTarget, new.imeAnimationTarget)

            raiseIfChanged("tappableElement", old.tappableElement, new.tappableElement)
            raiseIfChanged("tappableElementIgnoringVisibility", old.tappableElementIgnoringVisibility, new.tappableElementIgnoringVisibility)

            raiseIfChanged("systemGestures", old.systemGestures, new.systemGestures)
            raiseIfChanged("mandatorySystemGestures", old.mandatorySystemGestures, new.mandatorySystemGestures)

            raiseIfChanged("displayCutout", old.displayCutout, new.displayCutout)
            raiseIfChanged("waterfall", old.waterfall, new.waterfall)
        }

        _prevWindowInsetsSnapshot = new
    }

    private fun raiseIfChanged(name: String, old: WindowInsetsSnapshot, new: WindowInsetsSnapshot)
    {
        if (
            new.left != old.left
            || new.top != old.top
            || new.right != old.right
            || new.bottom != old.bottom
        )
        {
            notify("${name}WindowInsetsChanged", new)
        }
    }

    private fun notify(name: String) = raiseBridgeEvent(name)

    private fun notify(name: String, args: SerializableInstalledApp)
    {
        raiseBridgeEvent(name, Json.encodeToString(SerializableInstalledApp.serializer(), args))
    }

    private fun notify(name: String, args: AppRemovedEventArgs)
    {
        raiseBridgeEvent(name, Json.encodeToString(AppRemovedEventArgs.serializer(), args))
    }

    private fun notify(name: String, newValue: Boolean) = notify(name, ValueChangeEventArgs(newValue))
    private fun notify(name: String, newValue: String) = notify(name, ValueChangeEventArgs(newValue))
    private fun notify(name: String, newValue: WindowInsetsSnapshot) = notify(name, ValueChangeEventArgs(newValue))

    @JvmName("notifyBoolChanged")
    private fun notify(name: String, args: ValueChangeEventArgs<Boolean>)
    {
        raiseBridgeEvent(name, Json.encodeToString(ValueChangeEventArgs.serializer(Boolean.serializer()), args))
    }

    @JvmName("notifyStringChanged")
    private fun notify(name: String, args: ValueChangeEventArgs<String>)
    {
        raiseBridgeEvent(name, Json.encodeToString(ValueChangeEventArgs.serializer(String.serializer()), args))
    }

    @JvmName("notifyWindowInsetsChanged")
    private fun notify(name: String, args: ValueChangeEventArgs<WindowInsetsSnapshot>)
    {
        raiseBridgeEvent(name, Json.encodeToString(ValueChangeEventArgs.serializer(WindowInsetsSnapshot.serializer()), args))
    }

    private fun raiseBridgeEvent(name: String, argsString: String? = null)
    {
        try
        {
            webView?.evaluateJavascript("if (typeof onBridgeEvent === 'function') onBridgeEvent('$name', ${argsString ?: "undefined"})") {}
        }
        catch (ex: Exception)
        {
            Log.e(TAG, "notify", ex)
        }
    }
}
