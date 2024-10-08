package com.tored.bridgelauncher.services.settings

import kotlinx.coroutines.flow.StateFlow

typealias SettingsChangeListener = (newState: SettingsState, oldState: SettingsState) -> Unit

interface ISettingsStateProvider
{
    val settingsState: StateFlow<SettingsState>
}