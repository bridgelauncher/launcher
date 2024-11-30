package com.tored.bridgelauncher.ui2.home.composables

import android.app.Activity
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.tored.bridgelauncher.services.settings2.SystemBarAppearanceOptions
import com.tored.bridgelauncher.ui2.home.HomeScreenSystemUIState
import com.tored.bridgelauncher.utils.CurrentAndroidVersion

@Composable
fun SetHomeScreenSystemUIState(systemUIState: HomeScreenSystemUIState)
{
    val currentView = LocalView.current
    if (!currentView.isInEditMode)
    {
        val showWallpaper = systemUIState.drawSystemWallpaperBehindWebView
        val statusBarAppearance = systemUIState.statusBarAppearance
        val navigationBarAppearance = systemUIState.navigationBarAppearance

        val currentWindow = (currentView.context as? Activity)?.window
            ?: throw Exception("Attempt to access a window from outside an activity.")

        SideEffect()
        {
            val insetsController = WindowCompat.getInsetsController(currentWindow, currentView)

            if (showWallpaper)
                currentWindow.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER)
            else
                currentWindow.clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER)

            insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            if (CurrentAndroidVersion.supportsNavBarContrastEnforcement())
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