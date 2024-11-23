package com.tored.bridgelauncher.services.settings

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.tored.bridgelauncher.BridgeLauncherApplication
import com.tored.bridgelauncher.services.BridgeServices
import com.tored.bridgelauncher.ui2.settings.SettingsScreenVM
import com.tored.bridgelauncher.utils.bridgeLauncherApplication

class SettingsVM(
    private val _settingsHolder: SettingsHolder,
) : ViewModel()
{
    companion object
    {
        fun from(context: Application, serviceProvider: BridgeServices): SettingsScreenVM
        {
            with(serviceProvider)
            {
                return SettingsScreenVM(
                    _app = context.bridgeLauncherApplication,
                    _permsManager = storagePermsManager,
                    _settingsHolder = settingsHolder,
                    _mockExporter = mockExporter,
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