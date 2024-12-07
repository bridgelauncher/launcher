package com.tored.bridgelauncher.ui2.home

import androidx.compose.runtime.Immutable
import com.tored.bridgelauncher.services.settings2.SystemBarAppearanceOptions

@Immutable
data class HomeScreenSystemUIState(
    val statusBarAppearance: SystemBarAppearanceOptions,
    val navigationBarAppearance: SystemBarAppearanceOptions,
    val drawSystemWallpaperBehindWebView: Boolean,
)