package com.tored.bridgelauncher.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200,
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
fun BridgeLauncherTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit)
{
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
        content = content
    )
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
    get() = if(isLight) Color(0xA6000000) else Color(0xA6FFFFFF)

val Colors.checkedItemBg: Color
    get() = if(isLight) Color(0x26000000) else Color(0x26FFFFFF)