package com.tored.bridgelauncher.ui2.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.R
import com.tored.bridgelauncher.ui2.theme.BridgeLauncherThemeStateless
import com.tored.bridgelauncher.ui2.theme.textSec
import com.tored.bridgelauncher.utils.ComposableContent


@Composable
fun Tip(
    modifier: Modifier = Modifier,
    iconResId: Int = R.drawable.ic_tip,
    contentColor: Color = LocalContentColor.current,
    content: ComposableContent,
)
{
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    )
    {
        CompositionLocalProvider(LocalContentColor provides contentColor)
        {
            ResIcon(iconResId, inline = true, color = LocalContentColor.current)
            content()
        }
    }
}


// PREVIEWS

@Composable
fun TipPreview(content: ComposableContent)
{
    BridgeLauncherThemeStateless {
        Surface {
            Tip(
                modifier = Modifier.padding(8.dp),
                content = content,
                contentColor = MaterialTheme.colors.textSec
            )
        }
    }
}

@Composable
@PreviewLightDark
fun TipPreview01(modifier: Modifier = Modifier)
{
    TipPreview {
        Text("Test text", color = LocalContentColor.current)
    }
}