package com.tored.bridgelauncher.services.settings2

import androidx.datastore.preferences.core.MutablePreferences

fun <TPreference, TResult> MutablePreferences.resetBridgeSetting(
    bridgeSetting: BridgeSetting<TPreference, TResult>,
)
{
    this.remove(bridgeSetting.key)
}