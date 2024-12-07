package com.tored.bridgelauncher.ui2.home.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.ui2.theme.BridgeLauncherThemeStateless

@Composable
fun HomeScreenLoadingMessage(
    modifier: Modifier = Modifier
)
{
    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier,
    )
    {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        )
        {
            Text("Loading...")
            LinearProgressIndicator(
                color = MaterialTheme.colors.primary
            )
        }
    }
}


// PREVIEWS

@Composable
@PreviewLightDark
fun HomeScreenLoadingMessagePreview()
{
    BridgeLauncherThemeStateless {
        HomeScreenLoadingMessage()
    }
}