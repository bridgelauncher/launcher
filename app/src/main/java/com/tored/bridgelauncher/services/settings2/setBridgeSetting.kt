package com.tored.bridgelauncher.services.settings2

import androidx.datastore.preferences.core.MutablePreferences

fun <TPreference, TResult> MutablePreferences.setBridgeSetting(
    bridgeSetting: BridgeSetting<TPreference, TResult>,
    newValue: TResult,
)
{
    bridgeSetting.write(newValue).let {
        if (it == null)
            this.remove(bridgeSetting.key)
        else
            this[bridgeSetting.key] = it
    }
}