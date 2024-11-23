package com.tored.bridgelauncher.services.settings2

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.tored.bridgelauncher.services.settings.settingsDataStore
import com.tored.bridgelauncher.utils.collectAsStateButInViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

fun <TPreference, TResult> useBridgeSettingFlow(
    dataStore: DataStore<Preferences>,
    bridgeSetting: BridgeSetting<TPreference, TResult>,
): Flow<TResult>
{
    return dataStore.data.map { bridgeSetting.read(it[bridgeSetting.key]) }
}

fun <TPreference, TResult> useBridgeSettingStateFlow(
    dataStore: DataStore<Preferences>,
    coroutineScope: CoroutineScope,
    bridgeSetting: BridgeSetting<TPreference, TResult>,
): StateFlow<TResult>
{
    return useBridgeSettingFlow(dataStore, bridgeSetting)
        .stateIn(
            coroutineScope,
            SharingStarted.Eagerly,
            bridgeSetting.read(null)
        )
}

fun <TPreference, TResult> ViewModel.useBridgeSettingStateFlow(
    context: Context,
    bridgeSetting: BridgeSetting<TPreference, TResult>,
): StateFlow<TResult>
{
    return useBridgeSettingStateFlow(
        dataStore = context.settingsDataStore,
        coroutineScope = viewModelScope,
        bridgeSetting = bridgeSetting,
    )
}

fun <TPreference, TResult> ViewModel.useBridgeSettingState(
    context: Context,
    bridgeSetting: BridgeSetting<TPreference, TResult>,
): State<TResult>
{
    return collectAsStateButInViewModel(
        useBridgeSettingStateFlow(
            dataStore = context.settingsDataStore,
            coroutineScope = viewModelScope,
            bridgeSetting = bridgeSetting,
        )
    )
}

@Composable
fun <TPreference, TResult> rememberBridgeSettingState(
    bridgeSetting: BridgeSetting<TPreference, TResult>,
): State<TResult>
{
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val stateFlow = remember {
        useBridgeSettingStateFlow(
            dataStore = context.settingsDataStore,
            coroutineScope,
            bridgeSetting
        )
    }
    return stateFlow.collectAsStateWithLifecycle()
}