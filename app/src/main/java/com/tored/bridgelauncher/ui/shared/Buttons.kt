package com.tored.bridgelauncher.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.ui.theme.borders

@Composable
fun Btn(
    text: String,
    modifier: Modifier = Modifier,
    prefixIcon: Int? = null,
    suffixIcon: Int? = null,
    outlined: Boolean = false,
    onClick: (() -> Unit)? = null
)
{
    Surface(
        modifier = modifier
            .wrapContentSize()
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .then(if (outlined) Modifier.border(MaterialTheme.borders.soft, shape = MaterialTheme.shapes.medium) else Modifier),
        shape = MaterialTheme.shapes.medium,
        color = Color.Transparent,
        contentColor = MaterialTheme.colors.primary,
    )
    {
        Row(
            modifier = modifier
                .clickable(onClick = onClick ?: {}, enabled = onClick != null)
                .wrapContentSize()
                .padding(16.dp, 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        )
        {
            if (prefixIcon != null)
                ResIcon(iconResId = prefixIcon)

            Text(text, style = MaterialTheme.typography.button)

            if (suffixIcon != null)
                ResIcon(iconResId = suffixIcon)
        }
    }
}