package com.tored.bridgelauncher.ui.screens.settings

import android.app.Activity
import android.os.Build
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun SettingsScreenSetSystemBars()
{
    val currentView = LocalView.current
    if (!currentView.isInEditMode)
    {
        val currentWindow = (currentView.context as? Activity)?.window
            ?: throw Exception("Attempt to access a window from outside an activity.")

        val isLight = MaterialTheme.colors.isLight

        SideEffect()
        {
            val insetsController = WindowCompat.getInsetsController(currentWindow, currentView)
            WindowCompat.setDecorFitsSystemWindows(currentWindow, false)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                currentWindow.isNavigationBarContrastEnforced = false
            }

            currentWindow.statusBarColor = Color.Transparent.toArgb()
            currentWindow.navigationBarColor = Color.Transparent.toArgb()
            insetsController.isAppearanceLightStatusBars = isLight
            insetsController.isAppearanceLightNavigationBars = isLight
        }
    }
}