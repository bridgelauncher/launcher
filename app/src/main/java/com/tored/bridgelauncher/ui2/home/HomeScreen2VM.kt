package com.tored.bridgelauncher.ui2.home

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
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
import com.tored.bridgelauncher.api2.bridgetojs.BridgeToJSAPI
import com.tored.bridgelauncher.api2.jstobridge.JSToBridgeAPI
import com.tored.bridgelauncher.api2.server.BridgeServer
import com.tored.bridgelauncher.api2.webview.BridgeWebChromeClient
import com.tored.bridgelauncher.api2.webview.BridgeWebViewClient
import com.tored.bridgelauncher.services.BridgeServices
import com.tored.bridgelauncher.services.devconsole.DevConsoleMessagesHolder
import com.tored.bridgelauncher.services.displayshape.DisplayShapeHolder
import com.tored.bridgelauncher.services.lifecycleevents.LifecycleEventsHolder
import com.tored.bridgelauncher.services.perms.PermsHolder
import com.tored.bridgelauncher.services.settings2.BridgeSettings
import com.tored.bridgelauncher.services.settings2.setBridgeSetting
import com.tored.bridgelauncher.services.settings2.settingsDataStore
import com.tored.bridgelauncher.services.settings2.useBridgeSettingState
import com.tored.bridgelauncher.services.uimode.SystemUIModeHolder
import com.tored.bridgelauncher.services.windowinsetsholder.WindowInsetsHolder
import com.tored.bridgelauncher.ui2.home.bridgemenu.BridgeMenuActions
import com.tored.bridgelauncher.ui2.home.bridgemenu.BridgeMenuState
import com.tored.bridgelauncher.ui2.home.composables.onBridgeWebViewCreated
import com.tored.bridgelauncher.utils.bridgeLauncherApplication
import com.tored.bridgelauncher.utils.collectAsStateButInViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

private const val TAG = "HomeScreen2VM"

class HomeScreen2VM(
    private val _app: BridgeLauncherApplication,
    private val _permsHolder: PermsHolder,
    private val _bridgeServer: BridgeServer,
    private val _systemUIModeHolder: SystemUIModeHolder,
    private val _consoleMessages: DevConsoleMessagesHolder,
    private val _jsToBridgeInterface: JSToBridgeAPI,
    private val _bridgeToJSInterface: BridgeToJSAPI,
    private val _lifecycleEventsHolder: LifecycleEventsHolder,
    private val _windowInsetsHolder: WindowInsetsHolder,
    private val _displayShapeHolder: DisplayShapeHolder,
) : ViewModel()
{
    // SETTINGS STATE

    private val _currentProjDir by useBridgeSettingState(_app, BridgeSettings.currentProjDir)
    private val _drawSystemWallpaperBehindWebView by useBridgeSettingState(_app, BridgeSettings.drawSystemWallpaperBehindWebView)
    private val _statusBarAppearance by useBridgeSettingState(_app, BridgeSettings.statusBarAppearance)
    private val _navigationBarAppearance by useBridgeSettingState(_app, BridgeSettings.navigationBarAppearance)
    private val _drawWebViewOverscrollEffects = useBridgeSettingState(_app, BridgeSettings.drawWebViewOverscrollEffects)
    private val _showBridgeButton by useBridgeSettingState(_app, BridgeSettings.showBridgeButton)
    private val _showLaunchAppsWhenBridgeButtonCollapsed by useBridgeSettingState(_app, BridgeSettings.showLaunchAppsWhenBridgeButtonCollapsed)

    // we store the webview because we need to be able to refresh it
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

    private val _isBridgeServerReadyToServeState = collectAsStateButInViewModel(_bridgeServer.isReadyToServe, false)
    val projectState = derivedStateOf {

        val hasStoragePerms = _permsHolder.hasStoragePermsState.value
        val projDir = _currentProjDir

        if (!hasStoragePerms && projDir == null)
            IHomeScreenProjectState.FirstTimeLaunch

        else if (!hasStoragePerms)
            IHomeScreenProjectState.NoStoragePerm

        else if (projDir == null)
            IHomeScreenProjectState.NoProjectLoaded

        else if (!_isBridgeServerReadyToServeState.value)
            IHomeScreenProjectState.Initializing

        else
            IHomeScreenProjectState.ProjectLoaded(projDir)
    }

    private val _bridgeMenuIsExpandedStateFlow = MutableStateFlow(false)
    private val _bridgeMenuIsExpandedState = collectAsStateButInViewModel(_bridgeMenuIsExpandedStateFlow)

    val bridgeMenuState = derivedStateOf {
        BridgeMenuState(
            isShown = _showBridgeButton,
            isExpanded = _bridgeMenuIsExpandedState.value,
            showAppDrawerButtonWhenCollapsed = _showLaunchAppsWhenBridgeButtonCollapsed,
        )
    }

    val webViewDeps = BridgeWebViewDeps(
        webViewClient = BridgeWebViewClient(_bridgeServer),
        chromeClient = BridgeWebChromeClient(_consoleMessages),
        onCreated = {
            onBridgeWebViewCreated(it, _jsToBridgeInterface)
            webView = it
            _bridgeToJSInterface.webView = it
            _jsToBridgeInterface.webView = it
        },
        onDispose = {
            webView = null
            _bridgeToJSInterface.webView = null
            _jsToBridgeInterface.webView = null
        },
        drawOverscrollEffects = _drawWebViewOverscrollEffects
    )

    fun afterCreate(context: Context)
    {
        _jsToBridgeInterface.homeScreenContext = context
    }

    fun beforePause()
    {
        _lifecycleEventsHolder.notifyHomeScreenPaused()
    }

    fun onNewIntent()
    {
        _lifecycleEventsHolder.notifyHomeScreenReceivedNewIntent()
    }

    fun afterResume()
    {
        _lifecycleEventsHolder.notifyHomeScreenResumed()
        _permsHolder.notifyPermsMightHaveChanged()
    }

    fun onConfigurationChanged()
    {
        _systemUIModeHolder.onConfigurationChanged()
    }

    fun beforeDestroy()
    {
        _jsToBridgeInterface.homeScreenContext = null
    }

    val observerCallbacks = HomeScreenObserverCallbacks(
        onWindowInsetsChanged = { option, snapshot ->
            _windowInsetsHolder.notifyWindowInsetsChanged(option, snapshot)
        },
        onDisplayShapePathChanged = {
            _displayShapeHolder.notifyDisplayShapePathChanged(it)
        },
        onCutoutPathChanged = {
            _displayShapeHolder.notifyDisplayCutoutPathChanged(it)
        }
    )


    companion object
    {
        fun from(context: Application, serviceProvider: BridgeServices): HomeScreen2VM
        {
            with(serviceProvider)
            {
                return HomeScreen2VM(
                    _app = context.bridgeLauncherApplication,
                    _permsHolder = storagePermsHolder,
                    _bridgeServer = bridgeServer,
                    _consoleMessages = consoleMessagesHolder,
                    _jsToBridgeInterface = jsToBridgeInterface,
                    _bridgeToJSInterface = bridgeToJSInterface,
                    _windowInsetsHolder = windowInsetsHolder,
                    _lifecycleEventsHolder = lifecycleEventsHolder,
                    _displayShapeHolder = displayShapeHolder,
                    _systemUIModeHolder = systemUIModeHolder,
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