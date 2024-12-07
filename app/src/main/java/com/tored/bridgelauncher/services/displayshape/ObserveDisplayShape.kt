package com.tored.bridgelauncher.services.displayshape

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.tored.bridgelauncher.utils.CurrentAndroidVersion

typealias OnPathChangeFunc = (newPath: String?) -> Unit

@Composable
fun ObserveDisplayShape(
    onDisplayShapePathChanged: OnPathChangeFunc,
    onCutoutPathChanged: OnPathChangeFunc,
)
{
    val context = LocalContext.current
    val activity = context as? Activity

    if (activity != null)
    {
        val insets = activity.window.decorView.rootWindowInsets

        val displayShapePathString = if (CurrentAndroidVersion.supportsDisplayShape())
            insets.displayShape?.path?.toString()
        else
            null

        LaunchedEffect(displayShapePathString)
        {
            onDisplayShapePathChanged(displayShapePathString)
        }

        val displayCutoutPathString = if (CurrentAndroidVersion.supportsDisplayCutout())
            insets.displayCutout?.cutoutPath?.toString()
        else
            null

        LaunchedEffect(displayCutoutPathString)
        {
            onCutoutPathChanged(displayCutoutPathString)
        }
    }
}