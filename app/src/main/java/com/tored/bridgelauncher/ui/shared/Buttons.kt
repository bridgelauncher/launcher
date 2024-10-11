package com.tored.bridgelauncher.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.ui.theme.borders

@Composable
fun Btn(
    text: String,
    modifier: Modifier = Modifier,
    contentColor: Color? = null,
    prefixIcon: Int? = null,
    suffixIcon: Int? = null,
    outlined: Boolean = false,
    disabled: Boolean = false,
    onClick: () -> Unit,
)
{
    Surface(
        modifier = modifier
            .wrapContentSize()
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .run {
                if (outlined)
                    border(MaterialTheme.borders.soft, shape = MaterialTheme.shapes.medium)
                else
                    this
            },
        shape = MaterialTheme.shapes.medium,
        color = Color.Transparent,
        contentColor = contentColor ?: MaterialTheme.colors.primary,
    )
    {
        Row(
            modifier = modifier
                .clickable(onClick = onClick, enabled = !disabled)
                .alpha(if (disabled) 0.4f else 1f)
                .wrapContentSize()
                .padding(16.dp, 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        )
        {
            if (prefixIcon != null)
                ResIcon(iconResId = prefixIcon)

            Text(
                text,
                style = MaterialTheme.typography.button,
                color = LocalContentColor.current,
            )

            if (suffixIcon != null)
                ResIcon(iconResId = suffixIcon)
        }
    }
}