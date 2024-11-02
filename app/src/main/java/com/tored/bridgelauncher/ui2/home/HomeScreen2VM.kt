package com.tored.bridgelauncher.ui2.home

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import android.webkit.WebView
import androidx.compose.runtime.derivedStateOf
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.tored.bridgelauncher.BridgeLauncherApplication
import com.tored.bridgelauncher.ConsoleMessagesHolder
import com.tored.bridgelauncher.api.jsapi.BridgeToJSAPI
import com.tored.bridgelauncher.api.jsapi.JSToBridgeAPI
import com.tored.bridgelauncher.api.server.BridgeServer
import com.tored.bridgelauncher.services.BridgeServices
import com.tored.bridgelauncher.services.perms.PermsManager
import com.tored.bridgelauncher.services.apps.InstalledAppsHolder
import com.tored.bridgelauncher.services.iconpacks.InstalledIconPacksHolder
import com.tored.bridgelauncher.services.settings.SettingsState
import com.tored.bridgelauncher.services.settings.SettingsVM
import com.tored.bridgelauncher.services.settings.settingsDataStore
import com.tored.bridgelauncher.ui2.home.bridgemenu.BridgeMenuActions
import com.tored.bridgelauncher.ui2.home.bridgemenu.BridgeMenuState
import com.tored.bridgelauncher.ui2.home.composables.BridgeWebViewDeps
import com.tored.bridgelauncher.ui2.home.composables.onBridgeWebViewCreated
import com.tored.bridgelauncher.utils.collectAsStateButInViewModel
import com.tored.bridgelauncher.utils.readBool
import com.tored.bridgelauncher.utils.startBridgeAppDrawerActivity
import com.tored.bridgelauncher.utils.startBridgeSettingsActivity
import com.tored.bridgelauncher.utils.startDevConsoleActivity
import com.tored.bridgelauncher.utils.tryStartAndroidHomeSettingsActivity
import com.tored.bridgelauncher.utils.writeBool
import com.tored.bridgelauncher.webview.BridgeWebChromeClient
import com.tored.bridgelauncher.webview.BridgeWebViewClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

private const val TAG = "HomeScreen2VM"

class HomeScreen2VM(
    private val _context: Application,
    private val _permsManager: PermsManager,
    private val _settings: SettingsVM,
    private val _apps: InstalledAppsHolder,
    private val _iconPacks: InstalledIconPacksHolder,
    private val _consoleMessages: ConsoleMessagesHolder,
    private val _jsToBridgeAPI: JSToBridgeAPI,
    private val _bridgeToJSAPI: BridgeToJSAPI,
) : ViewModel()
{
    private val _bridgeServer = BridgeServer(
        _settings,
        _apps,
        _iconPacks,
    )

    // we store the webview because it needs to be affected by
    @SuppressLint("StaticFieldLeak")
    private var webView: WebView? = null

    val bridgeMenuActions = BridgeMenuActions(
        onWebViewRefreshRequest = { webView?.reload() },
        onOpenDevConsoleRequest = { _context.startDevConsoleActivity() },
        onSwitchLaunchersRequest = { _context.tryStartAndroidHomeSettingsActivity() },
        onOpenSettingsRequest = { _context.startBridgeSettingsActivity() },
        onHideBridgeButtonRequest = {
            viewModelScope.launch {
                _context.settingsDataStore.edit {
                    Log.d(TAG, "onHideBridgeButtonRequest: showBridgeButton: ${it.readBool(SettingsState::showBridgeButton, true)} => false")
                    it.writeBool(SettingsState::showBridgeButton, false)
                    Log.d(TAG, "onHideBridgeButtonRequest: showBridgeButton successfully set to false")
                }
                Log.d(TAG, "onHideBridgeButtonRequest: settingsDataStore.edit finished")
            }
        },
        onOpenAppDrawerRequest = { _context.startBridgeAppDrawerActivity() },
        onRequestIsExpandedChange = { _bridgeMenuIsExpandedStateFlow.value = it },
    )

    val systemUIState = derivedStateOf {
        val settings = _settings.settingsState.value
        HomeScreenSystemUIState(
            drawSystemWallpaperBehindWebView = settings.drawSystemWallpaperBehindWebView,
            statusBarAppearance = settings.statusBarAppearance,
            navigationBarAppearance = settings.navigationBarAppearance,
        )
    }

    val projectState = derivedStateOf {
        val settings = _settings.settingsState.value
        if (!_permsManager.hasStoragePermsState.value)
            IHomeScreenProjectState.NoStoragePerm
        else
        {
            val projDir = settings.currentProjDir
            if (projDir == null)
                IHomeScreenProjectState.NoProjectLoaded
            else
                IHomeScreenProjectState.ProjectLoaded(projDir)
        }
    }

    private val _bridgeMenuIsExpandedStateFlow = MutableStateFlow(false)
    private val _bridgeMenuIsExpandedState = collectAsStateButInViewModel(_bridgeMenuIsExpandedStateFlow)

    val bridgeMenuState = derivedStateOf {
        val settings = _settings.settingsState.value
        BridgeMenuState(
            isShown = settings.showBridgeButton,
            isExpanded = _bridgeMenuIsExpandedState.value,
            showAppDrawerButtonWhenCollapsed = settings.showBridgeButton
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
                    _context = context,
                    _permsManager = storagePermsManager,
                    _settings = settingsVM,
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