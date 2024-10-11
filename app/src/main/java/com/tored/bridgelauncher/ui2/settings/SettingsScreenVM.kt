package com.tored.bridgelauncher.ui2.settings

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.tored.bridgelauncher.BridgeLauncherApplication
import com.tored.bridgelauncher.services.BridgeServiceProvider
import com.tored.bridgelauncher.services.PermsManager
import com.tored.bridgelauncher.services.apps.InstalledAppsHolder
import com.tored.bridgelauncher.services.iconpacks.InstalledIconPacksHolder
import com.tored.bridgelauncher.services.settings.SettingsVM
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private val TAG = SettingsScreenVM::class.simpleName

class SettingsScreenVM(
    private val _context: Application,
    private val _permsManager: PermsManager,
    private val _settings: SettingsVM,
    private val _apps: InstalledAppsHolder,
    private val _iconPacks: InstalledIconPacksHolder,
) : ViewModel()
{
    init
    {
        startCollectingSettings()
    }

    private val _settingsState = MutableStateFlow(_settings.settingsState.value)
    val settingsState = _settingsState.asStateFlow()

    private fun startCollectingSettings() = viewModelScope.launch {
        _settings.settingsState.collectLatest {
            _settingsState.value = it
        }
    }


    companion object
    {
        fun from(context: Application, serviceProvider: BridgeServiceProvider): SettingsScreenVM
        {
            with(serviceProvider)
            {
                return SettingsScreenVM(
                    _context = context,
                    _permsManager = storagePermsManager,
                    _settings = settingsVM,
                    _apps = installedAppsHolder,
                    _iconPacks = installedIconPacksHolder,
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