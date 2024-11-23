package com.tored.bridgelauncher.ui2.appdrawer

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.tored.bridgelauncher.BridgeLauncherApplication
import com.tored.bridgelauncher.services.BridgeServices
import com.tored.bridgelauncher.services.apps.InstalledApp
import com.tored.bridgelauncher.services.apps.InstalledAppsHolder

class AppDrawerVM(
    private val _apps: InstalledAppsHolder,
) : ViewModel()
{
    private val _appListState = mutableStateOf(_apps.packageNameToInstalledAppMap.values.toList())
    val appListState = _appListState as State<List<InstalledApp>>

    companion object
    {
        fun from(context: Application, serviceProvider: BridgeServices): AppDrawerVM
        {
            with(serviceProvider)
            {
                return AppDrawerVM(
                    _apps = serviceProvider.installedAppsHolder,
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