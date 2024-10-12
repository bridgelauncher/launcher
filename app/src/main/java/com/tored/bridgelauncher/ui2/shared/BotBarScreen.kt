package com.tored.bridgelauncher.ui2.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.tored.bridgelauncher.R
import com.tored.bridgelauncher.ui.theme.BridgeLauncherThemeStateless
import com.tored.bridgelauncher.utils.ComposableContent

@Composable
fun BotBarScreen(
    modifier: Modifier = Modifier,
    leftActionIconResId: Int = R.drawable.ic_arrow_left,
    onLeftActionClick: () -> Unit,
    titleAreaContent: ComposableContent,
    content: ComposableContent,
)
{
    Column()
    {
        val scrollState = rememberScrollState()
        Box(
            contentAlignment = Alignment.TopStart,
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState),
        )
        {
            content()
        }
        SimpleToolbar(
            leftActionIconResId = leftActionIconResId,
            onLeftActionClick = onLeftActionClick,
        )
        {
            titleAreaContent()
        }
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
