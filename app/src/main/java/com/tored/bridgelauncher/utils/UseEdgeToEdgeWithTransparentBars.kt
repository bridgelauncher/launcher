package com.tored.bridgelauncher.utils

import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView

/**
 * it's absolutely insane that this is the best way to achieve system bars respecting the user-chosen theme
 * https://jadarma.github.io/blog/posts/2024/04/theme-aware-edge-to-edge-in-compose/#bonus-round-compose-all-the-way
 */
@Composable
fun UseEdgeToEdgeWithTransparentBars()
{
    val view = LocalView.current
    if (view.isInEditMode) return

    val isLight = MaterialTheme.colors.isLight

    SideEffect()
    {
        val barStyle = if (isLight) SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        else SystemBarStyle.dark(Color.TRANSPARENT)

        val activity = view.context as ComponentActivity

        activity.enableEdgeToEdge(barStyle, barStyle)
        activity.window.setNavigationBarContrastEnforcedIfSupported(false)
    }
}