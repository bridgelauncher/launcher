package com.tored.bridgelauncher.services.windowinsetsholder

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalDensity

typealias OnWindowInsetsChangedFunc = (option: WindowInsetsOptions, snapshot: WindowInsetsSnapshot) -> Unit

val emptyOnWIndowInsetsChangedFunc: OnWindowInsetsChangedFunc = { _, _ -> }

private const val TAG = "ObserveWindowInsets"

@Composable
fun ObserveWindowInsets(
    options: List<WindowInsetsOptions>,
    onWindowInsetsChanged: OnWindowInsetsChangedFunc,
)
{
    for (option in options)
    {
        val snapshot = option.getter(WindowInsets).getSnapshot(LocalDensity.current)

        LaunchedEffect(snapshot)
        {
//            Log.d(TAG, "ObserveWindowInsets.${option.rawValue}: changed")
            onWindowInsetsChanged(option, snapshot)
        }
    }
}
