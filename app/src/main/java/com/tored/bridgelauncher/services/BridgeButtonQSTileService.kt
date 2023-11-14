package com.tored.bridgelauncher.services

import android.content.ComponentName
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.tored.bridgelauncher.settings.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlin.coroutines.suspendCoroutine

class LifecycleImpl(override val currentState: State) : Lifecycle()
{
    override fun addObserver(observer: LifecycleObserver)
    {
        TODO("Not yet implemented")
    }

    override fun removeObserver(observer: LifecycleObserver)
    {
        TODO("Not yet implemented")
    }
}

@AndroidEntryPoint
class BridgeButtonQSTileService : TileService()
{
    private var _job = SupervisorJob()
    private var _scope = CoroutineScope(Dispatchers.Main + _job)
    private var _isListening = false;
    private var _shouldBeActive = false;

    override fun onCreate()
    {
        _scope.launch {
            applicationContext.settingsDataStore.data.collectLatest { prefs ->
                _shouldBeActive = prefs.readBool(SettingsState::showBridgeButton, true)
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

    fun updateTileIsAdded(isAdded: Boolean)
    {
        runBlocking {
            applicationContext.settingsDataStore.edit { prefs ->
                prefs.writeBool(SettingsState::qsTileIsAdded, isAdded)
            }
        }
    }

    override fun onStartListening()
    {
        _isListening = true;
        updateTileIfListening();
    }

    fun updateTileIfListening()
    {
        if (!_isListening) return

        if (_shouldBeActive)
        {
            qsTile.state = Tile.STATE_ACTIVE

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                qsTile.subtitle = "Shown"
        }
        else
        {
            qsTile.state = Tile.STATE_INACTIVE

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                qsTile.subtitle = "Hidden"
        }

        qsTile.updateTile()
    }

    override fun onClick()
    {
        val showButton = qsTile.state != Tile.STATE_ACTIVE

        _scope.launch {
            applicationContext.settingsDataStore.edit { prefs ->
                prefs.writeBool(SettingsState::showBridgeButton, showButton)
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