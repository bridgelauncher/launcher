package com.tored.bridgelauncher.ui2.settings

import android.app.Application
import androidx.lifecycle.ViewModel
import com.tored.bridgelauncher.services.PermsManager
import com.tored.bridgelauncher.services.settings.SettingsVM

class SettingsScreenVM(
    private val _context: Application,
    private val _permsManager: PermsManager,
    private val _settings: SettingsVM,
) : ViewModel()
{

}