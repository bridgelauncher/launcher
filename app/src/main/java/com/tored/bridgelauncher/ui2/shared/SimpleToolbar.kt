package com.tored.bridgelauncher.ui2.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.R
import com.tored.bridgelauncher.composables.ResIcon
import com.tored.bridgelauncher.ui.theme.BridgeLauncherThemeStateless
import com.tored.bridgelauncher.utils.ComposableContent

@Composable
fun SimpleToolbar(
    modifier: Modifier = Modifier,
    leftActionIconResId: Int = R.drawable.ic_arrow_left,
    onLeftActionClick: () -> Unit,
    titleContent: ComposableContent,
)
{
    Surface(
        color = MaterialTheme.colors.surface,
        elevation = 4.dp,
        modifier = modifier
            .fillMaxWidth()
    )
    {
        Row(
            modifier = Modifier
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        )
        {
            IconButton(
                onClick = onLeftActionClick
            )
            {
                ResIcon(leftActionIconResId)
            }

            Box(
                contentAlignment = Alignment.CenterEnd,
                modifier = Modifier
                    .weight(1f)
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
    }
}


// PREVIEWS

@Composable
fun SimpleToolbarPreview(
    text: String,
)
{
    BridgeLauncherThemeStateless {
        SimpleToolbar(
            onLeftActionClick = {},
            titleContent = {
                Text(text)
            }
        )
    }
}

@Composable
@PreviewLightDark
fun SimpleToolbarPreview01(modifier: Modifier = Modifier)
{
    SimpleToolbarPreview("Example title")
}