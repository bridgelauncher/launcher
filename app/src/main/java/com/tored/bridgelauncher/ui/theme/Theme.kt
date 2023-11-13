package com.tored.bridgelauncher.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tored.bridgelauncher.vms.SettingsVM
import com.tored.bridgelauncher.ThemeOptions

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
fun BridgeLauncherTheme(settingsVM: SettingsVM = viewModel(), content: @Composable () -> Unit)
{
    val state by settingsVM.settingsUIState.collectAsState()

    LaunchedEffect(settingsVM) { settingsVM.request() }

    val darkTheme = state.theme == ThemeOptions.Dark || isSystemInDarkTheme()

    val colors = if (darkTheme)
    {
        DarkColorPalette
    }
    else
    {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
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
    get() = if (isLight) Color(0xA6000000) else Color(0xA6FFFFFF)

val Colors.checkedItemBg: Color
    get() = if (isLight) Color(0x26000000) else Color(0x26FFFFFF)