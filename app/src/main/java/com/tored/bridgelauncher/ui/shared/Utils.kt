package com.tored.bridgelauncher.ui.shared

import android.app.Activity
import android.os.Build
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun SetSystemBarsForBotBarActivity()
{
    val currentView = LocalView.current
    if (!currentView.isInEditMode)
    {
        val currentWindow = (currentView.context as? Activity)?.window
            ?: throw Exception("Attempt to access a window from outside an activity.")

        val backgroundColor = MaterialTheme.colors.background.toArgb()
        val surfaceColor = MaterialTheme.colors.surface.toArgb()
        val isLight = MaterialTheme.colors.isLight

        SideEffect()
        {
            val insetsController = WindowCompat.getInsetsController(currentWindow, currentView)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                currentWindow.isNavigationBarContrastEnforced = false
            }

            currentWindow.statusBarColor = backgroundColor
            currentWindow.navigationBarColor = surfaceColor
            insetsController.isAppearanceLightStatusBars = isLight
            insetsController.isAppearanceLightNavigationBars = isLight
        }
    }
}