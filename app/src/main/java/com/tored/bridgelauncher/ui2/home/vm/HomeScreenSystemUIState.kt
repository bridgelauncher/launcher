package com.tored.bridgelauncher.ui2.home.vm

import androidx.compose.runtime.Immutable
import com.tored.bridgelauncher.services.settings.SystemBarAppearanceOptions

@Immutable
data class HomeScreenSystemUIState(
    val statusBarAppearance: SystemBarAppearanceOptions,
    val navigationBarAppearance: SystemBarAppearanceOptions,
    val drawSystemWallpaperBehindWebView: Boolean,
)