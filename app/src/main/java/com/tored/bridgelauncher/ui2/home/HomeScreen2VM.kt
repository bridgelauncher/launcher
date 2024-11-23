package com.tored.bridgelauncher.ui2.home

import android.annotation.SuppressLint
import android.app.Application
import android.webkit.WebView
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.tored.bridgelauncher.BridgeLauncherApplication
import com.tored.bridgelauncher.api.jsapi.BridgeToJSAPI
import com.tored.bridgelauncher.api.jsapi.JSToBridgeAPI
import com.tored.bridgelauncher.api.server.BridgeServer
import com.tored.bridgelauncher.services.BridgeServices
import com.tored.bridgelauncher.services.apps.InstalledAppsHolder
import com.tored.bridgelauncher.services.devconsole.DevConsoleMessagesHolder
import com.tored.bridgelauncher.services.iconpacks.InstalledIconPacksHolder
import com.tored.bridgelauncher.services.perms.PermsManager
import com.tored.bridgelauncher.services.settings.SettingsHolder
import com.tored.bridgelauncher.services.settings.settingsDataStore
import com.tored.bridgelauncher.services.settings2.BridgeSettings
import com.tored.bridgelauncher.services.settings2.setBridgeSetting
import com.tored.bridgelauncher.services.settings2.useBridgeSettingState
import com.tored.bridgelauncher.ui2.home.bridgemenu.BridgeMenuActions
import com.tored.bridgelauncher.ui2.home.bridgemenu.BridgeMenuState
import com.tored.bridgelauncher.ui2.home.composables.BridgeWebViewDeps
import com.tored.bridgelauncher.ui2.home.composables.onBridgeWebViewCreated
import com.tored.bridgelauncher.utils.bridgeLauncherApplication
import com.tored.bridgelauncher.utils.collectAsStateButInViewModel
import com.tored.bridgelauncher.webview.BridgeWebChromeClient
import com.tored.bridgelauncher.webview.BridgeWebViewClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

private const val TAG = "HomeScreen2VM"

class HomeScreen2VM(
    private val _app: BridgeLauncherApplication,
    private val _permsManager: PermsManager,
    private val _settings: SettingsHolder,
    private val _apps: InstalledAppsHolder,
    private val _iconPacks: InstalledIconPacksHolder,
    private val _consoleMessages: DevConsoleMessagesHolder,
    private val _jsToBridgeAPI: JSToBridgeAPI,
    private val _bridgeToJSAPI: BridgeToJSAPI,
) : ViewModel()
{
    private val _bridgeServer = BridgeServer(
        _app,
        _apps,
        _iconPacks,
    )

    // SETTINGS STATE

    private val _currentProjDir by useBridgeSettingState(_app, BridgeSettings.currentProjDir)
    private val _drawSystemWallpaperBehindWebView by useBridgeSettingState(_app, BridgeSettings.drawSystemWallpaperBehindWebView)
    private val _statusBarAppearance by useBridgeSettingState(_app, BridgeSettings.statusBarAppearance)
    private val _navigationBarAppearance by useBridgeSettingState(_app, BridgeSettings.navigationBarAppearance)
    private val _drawWebViewOverscrollEffects by useBridgeSettingState(_app, BridgeSettings.drawWebViewOverscrollEffects)
    private val _showBridgeButton by useBridgeSettingState(_app, BridgeSettings.showBridgeButton)
    private val _showLaunchAppsWhenBridgeButtonCollapsed by useBridgeSettingState(_app, BridgeSettings.showLaunchAppsWhenBridgeButtonCollapsed)

    // we store the webview because it needs to be affected by
    @SuppressLint("StaticFieldLeak")
    private var webView: WebView? = null

    val bridgeMenuActions = BridgeMenuActions(
        onWebViewRefreshRequest = { webView?.reload() },
        onHideBridgeButtonRequest = {
            viewModelScope.launch {
                _app.settingsDataStore.edit {
                    it.setBridgeSetting(BridgeSettings.showBridgeButton, false)
                }
            }
        },
        onRequestIsExpandedChange = { _bridgeMenuIsExpandedStateFlow.value = it },
    )

    val systemUIState = derivedStateOf {
        HomeScreenSystemUIState(
            drawSystemWallpaperBehindWebView = _drawSystemWallpaperBehindWebView,
            statusBarAppearance = _statusBarAppearance,
            navigationBarAppearance = _navigationBarAppearance,
        )
    }

    val projectState = derivedStateOf {
        if (!_permsManager.hasStoragePermsState.value)
            IHomeScreenProjectState.NoStoragePerm
        else
        {
            val projDir = _currentProjDir
            if (projDir == null)
                IHomeScreenProjectState.NoProjectLoaded
            else
                IHomeScreenProjectState.ProjectLoaded(projDir)
        }
    }

    private val _bridgeMenuIsExpandedStateFlow = MutableStateFlow(false)
    private val _bridgeMenuIsExpandedState = collectAsStateButInViewModel(_bridgeMenuIsExpandedStateFlow)

    val bridgeMenuState = derivedStateOf {
        BridgeMenuState(
            isShown = _showBridgeButton,
            isExpanded = _bridgeMenuIsExpandedState.value,
            showAppDrawerButtonWhenCollapsed = _showBridgeButton
        )
    }

    val webViewDeps = BridgeWebViewDeps(
        webViewClient = BridgeWebViewClient(_bridgeServer),
        chromeClient = BridgeWebChromeClient(_consoleMessages),
        onCreated = {
            onBridgeWebViewCreated(it, _jsToBridgeAPI)
            webView = it
            _bridgeToJSAPI.webView = it
            _jsToBridgeAPI.webView = it
        },
        onDispose = {
            webView = null
            _bridgeToJSAPI.webView = null
            _jsToBridgeAPI.webView = null
        }
    )

    fun beforePause()
    {
        _bridgeToJSAPI.raiseBeforePause()
    }

    fun afterResume()
    {
        _permsManager.notifyPermsMightHaveChanged()
        _bridgeToJSAPI.notifyCanSetSystemNightModeMightHaveChanged()
        _bridgeToJSAPI.raiseAfterResume()
    }

    companion object
    {
        fun from(context: Application, serviceProvider: BridgeServices): HomeScreen2VM
        {
            with(serviceProvider)
            {
                return HomeScreen2VM(
                    _app = context.bridgeLauncherApplication,
                    _permsManager = storagePermsManager,
                    _settings = settingsHolder,
                    _apps = installedAppsHolder,
                    _iconPacks = installedIconPacksHolder,
                    _consoleMessages = consoleMessagesHolder,
                    _jsToBridgeAPI = jsToBridgeAPI,
                    _bridgeToJSAPI = bridgeToJSAPI,
                )
            }
        }

        // https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-factories
        val Factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as BridgeLauncherApplication
                from(app, app.services)
            }
        }
    }
}