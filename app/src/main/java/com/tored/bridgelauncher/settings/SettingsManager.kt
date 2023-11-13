package com.tored.bridgelauncher.settings

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

val Context.settingsDataStore by preferencesDataStore("settings")

class SettingsManager @Inject constructor(@ApplicationContext appContext: Context)
{
    val store = appContext.settingsDataStore
}