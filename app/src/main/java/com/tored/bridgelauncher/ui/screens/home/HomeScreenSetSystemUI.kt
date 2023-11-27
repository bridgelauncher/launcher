package com.tored.bridgelauncher.ui.screens.home

import android.app.Activity
import android.os.Build
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.tored.bridgelauncher.settings.SettingsState
import com.tored.bridgelauncher.settings.SystemBarAppearanceOptions

@Composable
fun HomeScreenSetSystemUI(settingsState: SettingsState)
{
    val currentView = LocalView.current
    if (!currentView.isInEditMode)
    {
        val showWallpaper = settingsState.drawSystemWallpaperBehindWebView
        val statusBarAppearance = settingsState.statusBarAppearance
        val navigationBarAppearance = settingsState.navigationBarAppearance

        val currentWindow = (currentView.context as? Activity)?.window
            ?: throw Exception("Attempt to access a window from outside an activity.")

        SideEffect()
        {
            val insetsController = WindowCompat.getInsetsController(currentWindow, currentView)

            WindowCompat.setDecorFitsSystemWindows(currentWindow, false)

            if (showWallpaper)
                currentWindow.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER)
            else
                currentWindow.clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER)

            insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                currentWindow.isNavigationBarContrastEnforced = false
            }

            if (statusBarAppearance == SystemBarAppearanceOptions.Hide)
            {
                insetsController.hide(WindowInsetsCompat.Type.statusBars())
            }
            else
            {
                insetsController.show(WindowInsetsCompat.Type.statusBars())
                insetsController.isAppearanceLightStatusBars = statusBarAppearance == SystemBarAppearanceOptions.DarkIcons
            }

            if (navigationBarAppearance == SystemBarAppearanceOptions.Hide)
            {
                insetsController.hide(WindowInsetsCompat.Type.navigationBars())
            }
            else
            {
                insetsController.show(WindowInsetsCompat.Type.navigationBars())
                insetsController.isAppearanceLightNavigationBars = navigationBarAppearance == SystemBarAppearanceOptions.DarkIcons
            }
        }
    }
}