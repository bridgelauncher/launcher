package com.tored.bridgelauncher.ui2.home

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import android.webkit.WebView
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
import com.tored.bridgelauncher.services.BridgeServiceProvider
import com.tored.bridgelauncher.services.PermsManager
import com.tored.bridgelauncher.services.apps.InstalledAppsHolder
import com.tored.bridgelauncher.services.iconpacks.InstalledIconPacksHolder
import com.tored.bridgelauncher.services.settings.SettingsState
import com.tored.bridgelauncher.services.settings.SettingsVM
import com.tored.bridgelauncher.services.settings.settingsDataStore
import com.tored.bridgelauncher.ui2.home.bridgemenu.BridgeMenuActions
import com.tored.bridgelauncher.ui2.home.bridgemenu.BridgeMenuState
import com.tored.bridgelauncher.ui2.home.composables.BridgeWebViewDeps
import com.tored.bridgelauncher.ui2.home.composables.onBridgeWebViewCreated
import com.tored.bridgelauncher.utils.readBool
import com.tored.bridgelauncher.utils.startAndroidHomeSettingsActivity
import com.tored.bridgelauncher.utils.startBridgeAppDrawerActivity
import com.tored.bridgelauncher.utils.startBridgeSettingsActivity
import com.tored.bridgelauncher.utils.startDevConsoleActivity
import com.tored.bridgelauncher.utils.writeBool
import com.tored.bridgelauncher.webview.BridgeWebChromeClient
import com.tored.bridgelauncher.webview.BridgeWebViewClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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
    init
    {
        startCollectingSettings()
    }

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
        onSwitchLaunchersRequest = { _context.startAndroidHomeSettingsActivity() },
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
        onRequestIsExpandedChange = { _bridgeMenuState.value = bridgeMenuState.value.copy(isExpanded = it) },
    )

    private val _systemUIState = MutableStateFlow(getSystemUIState(_settings.settingsState.value))
    val systemUIState = _systemUIState.asStateFlow()

    private val _projectState = MutableStateFlow(getProjectState(_settings.settingsState.value))
    val projectState = _projectState.asStateFlow()

    private val _bridgeMenuState = MutableStateFlow(getBridgeMenuState(_settings.settingsState.value, isExpandedOverride = false))
    val bridgeMenuState = _bridgeMenuState.asStateFlow()

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

    private fun startCollectingSettings() = viewModelScope.launch {
        _settings.settingsState.collectLatest {
            Log.d(TAG, "_settings.settingsState.collectLatest(): settingsState changed, updating UI state")
            _systemUIState.value = getSystemUIState(it)
            _projectState.value = getProjectState(it)
            _bridgeMenuState.value = getBridgeMenuState(it)
        }
    }

    private fun getSystemUIState(settingsState: SettingsState): HomeScreenSystemUIState
    {
        return HomeScreenSystemUIState(
            drawSystemWallpaperBehindWebView = settingsState.drawSystemWallpaperBehindWebView,
            statusBarAppearance = settingsState.statusBarAppearance,
            navigationBarAppearance = settingsState.navigationBarAppearance,
        )
    }

    private fun getProjectState(settingsState: SettingsState): IHomeScreenProjectState
    {
        return if (!_permsManager.hasStoragePermsState.value)
            IHomeScreenProjectState.NoStoragePerm
        else
        {
            val projDir = settingsState.currentProjDir
            if (projDir == null)
                IHomeScreenProjectState.NoProjectLoaded
            else
                IHomeScreenProjectState.ProjectLoaded(projDir)
        }
    }

    private fun getBridgeMenuState(settingsState: SettingsState, isExpandedOverride: Boolean? = null): BridgeMenuState
    {
        return BridgeMenuState(
            isShown = settingsState.showBridgeButton,
            isExpanded = isExpandedOverride ?: _bridgeMenuState.value.isExpanded,
            showAppDrawerButtonWhenCollapsed = settingsState.showBridgeButton
        )
    }

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
        fun from(context: Application, serviceProvider: BridgeServiceProvider): HomeScreen2VM
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
                from(app, app.serviceProvider)
            }
        }
    }
}