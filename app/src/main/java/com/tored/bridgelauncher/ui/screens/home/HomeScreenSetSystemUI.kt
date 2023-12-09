package com.tored.bridgelauncher.ui.screens.home

import android.app.Activity
import android.os.Build
import android.util.Log
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.tored.bridgelauncher.settings.SettingsState
import com.tored.bridgelauncher.settings.SystemBarAppearanceOptions

private const val TAG = "HomeSystemUI"

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

            Log.d(TAG, "showWallpaper: $showWallpaper")
            if (showWallpaper)
                currentWindow.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER)
            else
                currentWindow.clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER)

            Log.d(TAG, "systemBarsBehavior = BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE")
            insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                Log.d(TAG, "isNavigationBarContrastEnforced = false")
                currentWindow.isNavigationBarContrastEnforced = false
            }

            if (statusBarAppearance == SystemBarAppearanceOptions.Hide)
            {
                Log.d(TAG, "insetsController.hide(statusBars)")
                insetsController.hide(WindowInsetsCompat.Type.statusBars())
            }
            else
            {
                Log.d(TAG, "insetsController.show(statusBars)")
                insetsController.show(WindowInsetsCompat.Type.statusBars())
                Log.d(TAG, "insetsController.isAppearanceLightStatusBars = $statusBarAppearance")
                insetsController.isAppearanceLightStatusBars = statusBarAppearance == SystemBarAppearanceOptions.DarkIcons
            }

            if (navigationBarAppearance == SystemBarAppearanceOptions.Hide)
            {
                Log.d(TAG, "insetsController.hide(navigationBars)")
                insetsController.hide(WindowInsetsCompat.Type.navigationBars())
            }
            else
            {
                Log.d(TAG, "insetsController.show(navigationBars)")
                insetsController.show(WindowInsetsCompat.Type.navigationBars())
                Log.d(TAG, "insetsController.isAppearanceLightNavigationBars = $statusBarAppearance")
                insetsController.isAppearanceLightNavigationBars = navigationBarAppearance == SystemBarAppearanceOptions.DarkIcons
            }
        }
    }
}