package com.tored.bridgelauncher.webview.jsapi

import android.util.Log
import android.webkit.WebView
import com.tored.bridgelauncher.SerializableInstalledApp
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

class BridgeToJSAPI
{
    private var _prevWindowInsetsSnapshot: WindowInsetsSnapshots? = null

    var webView: WebView? = null

    fun appInstalled(app: SerializableInstalledApp) = notify("appInstalled", app)
    fun appChanged(app: SerializableInstalledApp) = notify("appChanged", app)
    fun appRemoved(packageName: String) = notify("appRemoved", AppRemovedEventArgs(packageName))

    fun beforePause() = notify("beforePause")
    fun afterResume() = notify("afterResume")

    fun bridgeButtonVisibilityChanged(newValue: String) = notify("bridgeButtonVisibilityChanged", newValue)
    fun drawSystemWallpaperBehindWebViewChanged(newValue: Boolean) = notify("drawSystemWallpaperBehindWebViewChanged", newValue)
    fun bridgeThemeChanged(newValue: String) = notify("bridgeThemeChanged", newValue)
    fun statusBarAppearanceChanged(newValue: String) = notify("statusBarAppearanceChanged", newValue)
    fun navigationBarAppearanceChanged(newValue: String) = notify("navigationBarAppearanceChanged", newValue)
    fun canSetSystemNightModeChanged(newValue: Boolean) = notify("canSetSystemNightModeChanged", newValue)
    fun canLockScreenChanged(newValue: Boolean) = notify("canLockScreenChanged", newValue)

    fun systemNightModeChanged(newValue: String) = notify("systemNightModeChanged", newValue)

    fun windowInsetsSnapshotsChanged(new: WindowInsetsSnapshots)
    {
        val old = _prevWindowInsetsSnapshot

        if (old != null)
        {
            notifyIfChanged("statusBars", old.statusBars, new.statusBars)
            notifyIfChanged("statusBarsIgnoringVisibility", old.statusBarsIgnoringVisibility, new.statusBarsIgnoringVisibility)

            notifyIfChanged("navigationBars", old.navigationBars, new.navigationBars)
            notifyIfChanged("navigationBarsIgnoringVisibility", old.navigationBarsIgnoringVisibility, new.navigationBarsIgnoringVisibility)

            notifyIfChanged("captionBar", old.captionBar, new.captionBar)
            notifyIfChanged("captionBarIgnoringVisibility", old.captionBarIgnoringVisibility, new.captionBarIgnoringVisibility)

            notifyIfChanged("systemBars", old.systemBars, new.systemBars)
            notifyIfChanged("systemBarsIgnoringVisibility", old.systemBarsIgnoringVisibility, new.systemBarsIgnoringVisibility)

            notifyIfChanged("ime", old.ime, new.ime)
            notifyIfChanged("imeAnimationSource", old.imeAnimationSource, new.imeAnimationSource)
            notifyIfChanged("imeAnimationTarget", old.imeAnimationTarget, new.imeAnimationTarget)

            notifyIfChanged("tappableElement", old.tappableElement, new.tappableElement)
            notifyIfChanged("tappableElementIgnoringVisibility", old.tappableElementIgnoringVisibility, new.tappableElementIgnoringVisibility)

            notifyIfChanged("systemGestures", old.systemGestures, new.systemGestures)
            notifyIfChanged("mandatorySystemGestures", old.mandatorySystemGestures, new.mandatorySystemGestures)

            notifyIfChanged("displayCutout", old.displayCutout, new.displayCutout)
            notifyIfChanged("waterfall", old.waterfall, new.waterfall)
        }

        _prevWindowInsetsSnapshot = new
    }

    private fun notifyIfChanged(name: String, old: WindowInsetsSnapshot, new: WindowInsetsSnapshot)
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

fun String.escapeSinglequotes() = replace("'", "\\'")