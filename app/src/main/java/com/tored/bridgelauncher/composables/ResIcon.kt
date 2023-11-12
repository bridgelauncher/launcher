package com.tored.bridgelauncher.composables

import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun ResIcon(iconResId: Int, color: Color? = null, inline: Boolean = false)
{
    Icon(
        painter = painterResource(id = iconResId),
        contentDescription = null,
        modifier = Modifier.size(
            if (inline)
                with(LocalDensity.current) { LocalTextStyle.current.fontSize.toDp() }
            else
                24.dp
        ),
        tint = color
            ?: if (LocalContentColor.current == Color.Unspecified)
                LocalTextStyle.current.color
            else
                LocalContentColor.current,
    )
}