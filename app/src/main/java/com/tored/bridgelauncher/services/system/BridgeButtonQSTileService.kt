package com.tored.bridgelauncher.services.system

import android.content.ComponentName
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.datastore.preferences.core.edit
import com.tored.bridgelauncher.services.settings.settingsDataStore
import com.tored.bridgelauncher.services.settings2.BridgeSettings
import com.tored.bridgelauncher.services.settings2.setBridgeSetting
import com.tored.bridgelauncher.services.settings2.useBridgeSettingStateFlow
import com.tored.bridgelauncher.utils.CurrentAndroidVersion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class BridgeButtonQSTileService : TileService()
{
    private var _job = SupervisorJob()
    private var _scope = CoroutineScope(Dispatchers.Main)
    private var _isListening = false
    private var _shouldShowActiveState = false

    private val _showBridgeButton = useBridgeSettingStateFlow(
        dataStore = settingsDataStore,
        coroutineScope = _scope,
        bridgeSetting = BridgeSettings.showBridgeButton,
    )

    override fun onCreate()
    {
        _scope.launch {
            _showBridgeButton.collectLatest { showBridgeButton ->
                _shouldShowActiveState = showBridgeButton
                if (_isListening)
                {
                    updateTileIfListening()
                }
                else
                {
                    try
                    {
                        requestListeningState(applicationContext, ComponentName(applicationContext, BridgeButtonQSTileService::class.java))
                    }
                    catch (_: java.lang.Exception)
                    {

                    }
                }
            }
        }
    }

    override fun onTileAdded()
    {
        updateTileIsAdded(true)
    }

    private fun updateTileIsAdded(isAdded: Boolean)
    {
        _scope.launch {
            applicationContext.settingsDataStore.edit { prefs ->
                prefs.setBridgeSetting(BridgeSettings.isQSTileAdded, isAdded)
            }
        }
    }

    override fun onStartListening()
    {
        _isListening = true
        updateTileIfListening()
    }

    private fun updateTileIfListening()
    {
        if (!_isListening) return

        if (_shouldShowActiveState)
        {
            qsTile.state = Tile.STATE_ACTIVE

            if (CurrentAndroidVersion.supportsQSTileSubtitle())
                qsTile.subtitle = "Shown"
        }
        else
        {
            qsTile.state = Tile.STATE_INACTIVE

            if (CurrentAndroidVersion.supportsQSTileSubtitle())
                qsTile.subtitle = "Hidden"
        }

        qsTile.updateTile()
    }

    override fun onClick()
    {
        val showButton = qsTile.state != Tile.STATE_ACTIVE

        runBlocking {
            applicationContext.settingsDataStore.edit { prefs ->
                prefs.setBridgeSetting(BridgeSettings.showBridgeButton, showButton)
            }
        }
    }

    override fun onStopListening()
    {
        _isListening = false
    }

    override fun onDestroy()
    {
        _job.cancel()
    }

    override fun onTileRemoved()
    {
        updateTileIsAdded(false)
    }
}