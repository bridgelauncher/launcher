package com.tored.bridgelauncher.ui2.appdrawer.composables.appcontextmenu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.ui2.shared.ResIcon

@Composable
fun AppContextMenuItem(
    iconResId: Int,
    label: String,
    onClick: () -> Unit,
)
{
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.Start),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(end = 32.dp, start = 8.dp),
    )
    {
        Box(
            modifier = Modifier
                .size(48.dp),
            contentAlignment = Alignment.Center,
        )
        {
            ResIcon(iconResId)
        }
        Text(
            label,
            modifier = Modifier.width(IntrinsicSize.Max),
        )
    }
}