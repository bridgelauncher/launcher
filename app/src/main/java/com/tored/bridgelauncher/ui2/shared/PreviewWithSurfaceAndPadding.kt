package com.tored.bridgelauncher.ui2.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.ui.theme.BridgeLauncherThemeStateless
import com.tored.bridgelauncher.utils.ComposableContent

@Composable
fun PreviewWithSurfaceAndPadding(content: ComposableContent)
{
    BridgeLauncherThemeStateless {
        Surface {
            Box(modifier = Modifier.padding(16.dp))
            {
                content()
            }
        }
    }
}