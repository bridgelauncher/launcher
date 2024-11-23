package com.tored.bridgelauncher.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tored.bridgelauncher.services.settings.SettingsHolder
import com.tored.bridgelauncher.services.settings.SettingsState
import com.tored.bridgelauncher.services.settings.SystemBarAppearanceOptions
import com.tored.bridgelauncher.ui.shared.CheckboxField
import com.tored.bridgelauncher.utils.bridgeLauncherApplication
import com.tored.bridgelauncher.utils.displayNameFor
import com.tored.bridgelauncher.utils.writeBool
import com.tored.bridgelauncher.utils.writeEnum
import kotlin.reflect.KProperty1

@Composable
fun SettingsCheckboxFieldFor(prop: KProperty1<SettingsState, Boolean>, vm: SettingsHolder = SettingsHolder(LocalContext.current.bridgeLauncherApplication))
{
    val uiState by vm.settingsState.collectAsStateWithLifecycle()

    CheckboxField(
        label = displayNameFor(prop),
        isChecked = prop.getValue(uiState, prop),
        onCheckedChange = { isChecked ->
            vm.edit {
                writeBool(prop, isChecked)
            }
        }
    )
}

@Composable
fun SettingsSystemBarOptionsFieldFor(prop: KProperty1<SettingsState, SystemBarAppearanceOptions>, vm: SettingsHolder = SettingsHolder(LocalContext.current.bridgeLauncherApplication))
{
    val uiState by vm.settingsState.collectAsStateWithLifecycle()

    SystemBarAppearanceOptionsField(
        label = displayNameFor(prop),
        selectedOption = prop.getValue(uiState, prop),
        onChange = { value ->
            vm.edit {
                writeEnum(prop, value)
            }
        }
    )
}