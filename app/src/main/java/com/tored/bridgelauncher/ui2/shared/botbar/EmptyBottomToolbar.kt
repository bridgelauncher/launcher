package com.tored.bridgelauncher.ui2.shared.botbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.ui2.theme.botBar
import com.tored.bridgelauncher.ui2.shared.PreviewWithSurfaceAndPadding
import com.tored.bridgelauncher.utils.ComposableContent

@Composable
fun EmptyBottomToolbar(
    modifier: Modifier = Modifier,
    content: ComposableContent,
)
{
    Surface(
        color = MaterialTheme.colors.surface,
        shape = MaterialTheme.shapes.botBar,
        elevation = 4.dp,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    )
    {
        Column()
        {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
            )
            {
                content()
            }

            Spacer(
                modifier = Modifier
                    .background(Color.Magenta)
                    .windowInsetsBottomHeight(WindowInsets.navigationBars)
            )
        }
    }
}


// PREVIEWS

@Composable
fun EmptyBottomToolbarPreview()
{
    PreviewWithSurfaceAndPadding {
        EmptyBottomToolbar {}
    }
}

@Composable
@PreviewLightDark
fun EmptyBottomToolbarPreview_01()
{
    EmptyBottomToolbarPreview()
}