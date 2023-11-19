package com.tored.bridgelauncher.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.LocalElevationOverlay
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tored.bridgelauncher.ComposableContent
import com.tored.bridgelauncher.ThemeOptions
import com.tored.bridgelauncher.settings.SettingsVM

private val DarkColorPalette = darkColors(
    primary = GreenA200,
    primaryVariant = GreenA200,
    secondary = GreenA200,
    background = Color(0xff212121),
    surface = Color(0xff000000),
)

private val LightColorPalette = lightColors(
    primary = GreenA700,
    primaryVariant = GreenA700,
    secondary = GreenA700,
    background = Color(0xfff7f7f7),
    surface = Color(0xffffffff),
    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun BridgeLauncherTheme(settingsVM: SettingsVM = viewModel(), content: ComposableContent)
{
    val state by settingsVM.settingsUIState.collectAsStateWithLifecycle()
    LaunchedEffect(settingsVM) { settingsVM.request() }
    val useDarkTheme = state.theme == ThemeOptions.Dark || isSystemInDarkTheme()
    BridgeLauncherThemeStateless(
        useDarkTheme = useDarkTheme,
        content = content,
    )
}

@Composable
fun BridgeLauncherThemeStateless(
    useDarkTheme: Boolean,
    content: ComposableContent
)
{
    MaterialTheme(
        colors = if (useDarkTheme) DarkColorPalette else LightColorPalette,
        typography = Typography,
        shapes = Shapes,
    )
    {
        CompositionLocalProvider(
            LocalElevationOverlay provides null,
            content = content
        )
    }
}

data class Borders(val soft: BorderStroke)

private val LightBorders: Borders = Borders(
    soft = BorderStroke(width = 1.dp, color = Color(0x26000000)),
)

private val DarkBorders = Borders(
    soft = BorderStroke(width = 1.dp, color = Color(0x26ffffff)),
)

val MaterialTheme.borders: Borders
    @Composable
    get() = if (colors.isLight) LightBorders else DarkBorders

val Colors.textSec: Color
    get() = if (isLight) Color(0x8C000000) else Color(0x8CFFFFFF)

val Colors.textPlaceholder: Color
    get() = if (isLight) Color(0x65000000) else Color(0x66FFFFFF)

val Colors.checkedItemBg: Color
    get() = if (isLight) Color(0x26000000) else Color(0x26FFFFFF)
