package com.tored.bridgelauncher.ui2.shared

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.tored.bridgelauncher.utils.CurrentAndroidVersion

/** Pass `null` to leave the state unchanged. */
@Composable
fun UpdateSystemBars(
    statusBarColor: Color?,
    useLightStatusBarIcons: Boolean?,
    navigationBarColor: Color?,
    useLightNavigationBarIcons: Boolean?,
    useNavigationBarContrastEnforcement: Boolean? = false,
)
{
    val currentView = LocalView.current
    if (!currentView.isInEditMode)
    {
        val currentWindow = (currentView.context as? Activity)?.window
            ?: throw Exception("Attempt to access a window from outside an activity.")

        val insetsController = WindowCompat.getInsetsController(currentWindow, currentView)

        SideEffect()
        {
            if (
                CurrentAndroidVersion.supportsNavBarContrastEnforcement()
                && useNavigationBarContrastEnforcement != null
            )
            {
                currentWindow.isNavigationBarContrastEnforced = useNavigationBarContrastEnforcement
            }

            if (statusBarColor != null) currentWindow.statusBarColor = statusBarColor.toArgb()
            if (navigationBarColor != null) currentWindow.navigationBarColor = navigationBarColor.toArgb()
            if (useLightStatusBarIcons != null) insetsController.isAppearanceLightStatusBars = useLightStatusBarIcons
            if (useLightNavigationBarIcons != null) insetsController.isAppearanceLightNavigationBars = useLightNavigationBarIcons
        }
    }
}