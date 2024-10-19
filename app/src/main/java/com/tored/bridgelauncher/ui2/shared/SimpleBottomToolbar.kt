package com.tored.bridgelauncher.ui2.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.R
import com.tored.bridgelauncher.composables.ResIcon
import com.tored.bridgelauncher.ui.theme.BridgeLauncherThemeStateless
import com.tored.bridgelauncher.ui.theme.botBar
import com.tored.bridgelauncher.utils.ComposableContent

@Composable
fun SimpleBottomToolbar(
    modifier: Modifier = Modifier,
    leftActionIconResId: Int = R.drawable.ic_arrow_left,
    onLeftActionClick: () -> Unit,
    titleContent: ComposableContent,
)
{
    Surface(
        color = MaterialTheme.colors.surface,
        shape = MaterialTheme.shapes.botBar,
        elevation = 4.dp,
        modifier = modifier
            .fillMaxWidth()
    )
    {
        Column()
        {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
            )
            {
                IconButton(
                    onClick = onLeftActionClick,
                    modifier = modifier.align(Alignment.CenterStart)
                )
                {
                    ResIcon(leftActionIconResId)
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp, 4.dp),
                )
                {
                    ProvideTextStyle(
                        value = MaterialTheme.typography.h6.copy(
                            textAlign = TextAlign.End
                        ),
                    )
                    {
                        titleContent()
                    }
                }
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
fun SimpleBottomToolbarPreview(
    text: String,
)
{
    BridgeLauncherThemeStateless {
        SimpleBottomToolbar(
            onLeftActionClick = {},
            titleContent = {
                Text(text)
            }
        )
    }
}

@Composable
@PreviewLightDark
fun SimpleBottomToolbarPreview01(modifier: Modifier = Modifier)
{
    SimpleBottomToolbarPreview("Example title")
}