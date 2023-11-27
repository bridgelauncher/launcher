package com.tored.bridgelauncher.ui.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tored.bridgelauncher.settings.SettingsState
import com.tored.bridgelauncher.settings.SettingsVM
import com.tored.bridgelauncher.settings.SystemBarAppearanceOptions
import com.tored.bridgelauncher.ui.shared.CheckboxField
import com.tored.bridgelauncher.utils.displayNameFor
import com.tored.bridgelauncher.utils.writeBool
import com.tored.bridgelauncher.utils.writeEnum
import kotlin.reflect.KProperty1

@Composable
fun SettingsCheckboxFieldFor(prop: KProperty1<SettingsState, Boolean>, vm: SettingsVM = viewModel())
{
    val uiState by vm.settingsUIState.collectAsStateWithLifecycle()

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
fun SettingsSystemBarOptionsFieldFor(prop: KProperty1<SettingsState, SystemBarAppearanceOptions>, vm: SettingsVM = viewModel())
{
    val uiState by vm.settingsUIState.collectAsStateWithLifecycle()

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