package com.tored.bridgelauncher.api2.bridgetojs

import android.content.Context
import android.util.Log
import android.webkit.WebView
import com.tored.bridgelauncher.api2.bridgetojs.events.apps.AppChangedEvent
import com.tored.bridgelauncher.api2.bridgetojs.events.apps.AppInstalledEvent
import com.tored.bridgelauncher.api2.bridgetojs.events.apps.AppRemovedEvent
import com.tored.bridgelauncher.api2.bridgetojs.events.lifecycle.AfterResumeEvent
import com.tored.bridgelauncher.api2.bridgetojs.events.lifecycle.BeforePauseEvent
import com.tored.bridgelauncher.api2.bridgetojs.events.lifecycle.NewIntentEvent
import com.tored.bridgelauncher.api2.bridgetojs.events.perms.CanLockScreenChangedEvent
import com.tored.bridgelauncher.api2.bridgetojs.events.perms.CanRequestSystemNightModeChangedEvent
import com.tored.bridgelauncher.api2.bridgetojs.events.settings.BridgeButtonVisibilityChangedEvent
import com.tored.bridgelauncher.api2.bridgetojs.events.settings.BridgeThemeChangedEvent
import com.tored.bridgelauncher.api2.bridgetojs.events.settings.DrawSystemWallpaperBehindWebViewChangedEvent
import com.tored.bridgelauncher.api2.bridgetojs.events.settings.NavigationBarAppearanceChangedEvent
import com.tored.bridgelauncher.api2.bridgetojs.events.settings.OverscrollEffectsChangedEvent
import com.tored.bridgelauncher.api2.bridgetojs.events.settings.StatusBarAppearanceChangedEvent
import com.tored.bridgelauncher.api2.bridgetojs.events.systemuimode.SystemNightModeChangedEvent
import com.tored.bridgelauncher.api2.bridgetojs.events.windowinsets.WindowInsetsChangedEvent
import com.tored.bridgelauncher.api2.shared.BridgeButtonVisibilityStringOptions
import com.tored.bridgelauncher.api2.shared.BridgeThemeStringOptions
import com.tored.bridgelauncher.api2.shared.OverscrollEffectsStringOptions
import com.tored.bridgelauncher.api2.shared.SystemBarAppearanceStringOptions
import com.tored.bridgelauncher.services.apps.InstalledAppListChangeEvent
import com.tored.bridgelauncher.services.apps.InstalledAppsHolder
import com.tored.bridgelauncher.services.lifecycleevents.LifecycleEventsHolder
import com.tored.bridgelauncher.services.perms.PermsHolder
import com.tored.bridgelauncher.services.settings2.BridgeSetting
import com.tored.bridgelauncher.services.settings2.BridgeSettings
import com.tored.bridgelauncher.services.settings2.settingsDataStore
import com.tored.bridgelauncher.services.settings2.useBridgeSettingStateFlow
import com.tored.bridgelauncher.services.uimode.SystemUIModeHolder
import com.tored.bridgelauncher.services.windowinsetsholder.WindowInsetsHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

private val TAG = BridgeToJSAPI::class.simpleName

class BridgeToJSAPI(
    private val _app: Context,
    private val _apps: InstalledAppsHolder,
    private val _perms: PermsHolder,
    private val _insets: WindowInsetsHolder,
    private val _systemUIMode: SystemUIModeHolder,
    private val _lifecycleEventsHolder: LifecycleEventsHolder,
)
{
    private val _scope = CoroutineScope(Dispatchers.Main)

    var webView: WebView? = null

    private fun sendBridgeEvent(model: IBridgeEventModel)
    {
        try
        {
            when (val wv = webView)
            {
                null -> Log.w(TAG, "sendBridgeEvent(${model.name}): webView is null, ignoring event")
                else ->
                {
                    wv.evaluateJavascript("if (typeof onBridgeEvent === 'function') onBridgeEvent(${model.getJson()})") { }
                    // this leads to a lot of log spam from windowinsets changes
//                    Log.w(TAG, "sendBridgeEvent(${model.name}): OK")
                }
            }
        }
        catch (ex: Exception)
        {
            Log.e(BridgeToJSAPI::class.simpleName, "sendBridgeEvent(${model.name}): failure", ex)
        }
    }

    private fun startCollectingEvents() = _scope.launch {

        Log.d(TAG, "startCollectingEvents")

        with(_apps)
        {
            Log.d(TAG, "appListChangeEventFlow before onCollect")
            onCollect(appListChangeEventFlow) {
                Log.d(TAG, "appListChangeEventFlow collected: $it")
                when (it)
                {
                    is InstalledAppListChangeEvent.Added ->
                    {
                        if (!it.isFromInitialLoad)
                            AppInstalledEvent(it.newApp.toSerializable())
                        else
                            null
                    }

                    is InstalledAppListChangeEvent.Changed -> AppChangedEvent(it.newApp.toSerializable())
                    is InstalledAppListChangeEvent.Removed -> AppRemovedEvent(it.packageName)
                }
            }
        }

        with(BridgeSettings)
        {
            onCollectSetting(showBridgeButton) { BridgeButtonVisibilityChangedEvent(BridgeButtonVisibilityStringOptions.fromShowBridgeButton(it)) }
            onCollectSetting(drawSystemWallpaperBehindWebView) { DrawSystemWallpaperBehindWebViewChangedEvent(it) }
            onCollectSetting(drawWebViewOverscrollEffects) { OverscrollEffectsChangedEvent(OverscrollEffectsStringOptions.fromDrawWebViewOverscrollEffects(it)) }
            onCollectSetting(theme) { BridgeThemeChangedEvent(BridgeThemeStringOptions.fromBridgeTheme(it)) }
            onCollectSetting(statusBarAppearance) { StatusBarAppearanceChangedEvent(SystemBarAppearanceStringOptions.fromSystemBarAppearance(it)) }
            onCollectSetting(navigationBarAppearance) { NavigationBarAppearanceChangedEvent(SystemBarAppearanceStringOptions.fromSystemBarAppearance(it)) }
        }

        with(_perms)
        {
            onCollect(canSetSystemNightModeState) { CanRequestSystemNightModeChangedEvent(it) }
            onCollect(canProjectsLockScreen) { CanLockScreenChangedEvent(it) }
        }

        with(_systemUIMode)
        {
            onCollect(systemNightMode) { SystemNightModeChangedEvent(it) }
        }

        with(_insets)
        {
            stateFlowMap.forEach { (option, stateFlow) ->
                launch {
                    stateFlow.collect { snapshot ->
                        sendBridgeEvent(WindowInsetsChangedEvent.fromSnapshot(option, snapshot))
                    }
                }
            }
        }

        with(_lifecycleEventsHolder)
        {
            onCollect(homeScreenBeforePause) { BeforePauseEvent() }
            onCollect(homeScreenNewIntent) { NewIntentEvent() }
            onCollect(homeScreenAfterResume) { AfterResumeEvent() }
        }
    }

    private fun <T> CoroutineScope.onCollect(flow: Flow<T>, newValueToEvent: (newValue: T) -> BridgeEventModel?)
    {
        launch {
            flow.collect {
                newValueToEvent(it)?.let { ev ->
                    sendBridgeEvent(ev)
                }
            }
        }
    }

    private fun <TPreference, TResult> CoroutineScope.onCollectSetting(
        setting: BridgeSetting<TPreference, TResult>,
        newValueToEvent: (newValue: TResult) -> BridgeEventModel,
    )
    {
        val flow = useBridgeSettingStateFlow(_app.settingsDataStore, _scope, setting)
        launch {
            flow.collect {
                sendBridgeEvent(newValueToEvent(it))
            }
        }
    }

    fun startup()
    {
        startCollectingEvents()
    }
}