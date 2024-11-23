package com.tored.bridgelauncher.ui2.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsEndWidth
import androidx.compose.foundation.layout.windowInsetsStartWidth
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.R
import com.tored.bridgelauncher.ui.theme.BridgeLauncherThemeStateless
import com.tored.bridgelauncher.utils.ComposableContent

@Composable
fun BotBarScreen(
    modifier: Modifier = Modifier,
    leftActionIconResId: Int = R.drawable.ic_arrow_left,
    onLeftActionClick: () -> Unit,
    titleAreaContent: ComposableContent,
    rightContent: ComposableContent? = null,
    content: ComposableContent,
)
{
    Row(modifier = modifier)
    {
        val density = LocalDensity.current
        val dir = LocalLayoutDirection.current

        Spacer(modifier = Modifier.windowInsetsStartWidth(WindowInsets.safeContent))
        Column(
            modifier = Modifier
                .weight(1f)
                .consumeWindowInsets(
                    PaddingValues(
                        start = WindowInsets.safeContent.getLeft(density, dir).dp,
                        end = WindowInsets.safeContent.getRight(density, dir).dp,
                    )
                )
        )
        {

            Box(modifier = Modifier.weight(1f))
            {
                content()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsTopHeight(WindowInsets.safeContent)
                        .background(
                            if (MaterialTheme.colors.isLight)
                                MaterialTheme.colors.background.copy(alpha = 0.9f)
                            else
                                MaterialTheme.colors.background.copy(alpha = 0.9f)
                        )
                        .align(Alignment.TopStart)
                )
            }
            SimpleBottomToolbar(
                leftActionIconResId = leftActionIconResId,
                onLeftActionClick = onLeftActionClick,
                rightContent = rightContent,
            )
            {
                titleAreaContent()
            }
        }
        Spacer(modifier = Modifier.windowInsetsEndWidth(WindowInsets.safeContent))
    }
}


// PREVIEWS

@Composable
fun BotBarScreenPreview(
    title: String,
)
{
    BridgeLauncherThemeStateless {
        BotBarScreen(
            onLeftActionClick = { },
            titleAreaContent = {
                Text(title)
            }
        ) {

        }
    }
}

@Composable
@PreviewLightDark
fun BotBarScreenPreview01(modifier: Modifier = Modifier)
{
    BotBarScreenPreview(
        "Example title"
    )
}
