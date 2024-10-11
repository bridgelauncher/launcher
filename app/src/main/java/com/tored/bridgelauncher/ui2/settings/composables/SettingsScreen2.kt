package com.tored.bridgelauncher.ui2.settings.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tored.bridgelauncher.ui.theme.BridgeLauncherThemeStateless
import com.tored.bridgelauncher.ui2.settings.SettingsScreenVM

@Composable
fun SettingsScreen2(vm: SettingsScreenVM = viewModel())
{
        
}


// PREVIEWS

@Composable
@PreviewLightDark
fun SettingsScreen2Preview01()
{
    BridgeLauncherThemeStateless {
        SettingsScreen2()
    }
}