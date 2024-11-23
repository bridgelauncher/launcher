package com.tored.bridgelauncher.ui2.appdrawer

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.tored.bridgelauncher.BridgeLauncherApplication
import com.tored.bridgelauncher.services.BridgeServices
import com.tored.bridgelauncher.services.apps.InstalledApp
import com.tored.bridgelauncher.services.apps.InstalledAppsHolder
import com.tored.bridgelauncher.services.iconcache.IconCache
import com.tored.bridgelauncher.services.iconpacks.IconPack

class AppDrawerVM(
    private val _apps: InstalledAppsHolder,
    private val _iconCache: IconCache,
) : ViewModel()
{
    private val _appList = mutableStateOf(_apps.packageNameToInstalledAppMap.values.toList())

    private val _searchString = mutableStateOf("")
    val searchString = _searchString as State<String>

    fun updateSearchStringRequest(newSearchString: String)
    {
        _searchString.value = newSearchString
    }

    val filteredApps = derivedStateOf {
        val s = InstalledApp.simplifyLabel(_searchString.value)
        _appList.value.filter { it.labelSimplified.contains(s) || it.packageName.contains(s)
        }
    }

    suspend fun getIcon(iconPack: IconPack?, app: InstalledApp): ImageBitmap
    {
        return _iconCache.getIcon(null, app.packageName, app.lastModifiedNanoTime)
    }

    companion object
    {
        fun from(context: Application, serviceProvider: BridgeServices): AppDrawerVM
        {
            with(serviceProvider)
            {
                return AppDrawerVM(
                    _apps = installedAppsHolder,
                    _iconCache = iconCache,
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