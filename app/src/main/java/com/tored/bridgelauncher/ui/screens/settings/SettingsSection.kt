package com.tored.bridgelauncher.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.composables.ResIcon
import com.tored.bridgelauncher.utils.ComposableContent

@Composable
fun SettingsSection(label: String, iconResId: Int, content: ComposableContent)
{
    Column(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth(),
    )
    {
        SettingsSectionHeader(label = label, iconResId = iconResId)
        Column(
            modifier = Modifier
                .padding(start = 16.dp, top = 8.dp, bottom = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        )
        {
            content()
        }
    }
}

@Composable
fun SettingsSectionHeader(label: String, iconResId: Int)
{
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .padding(20.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp, alignment = Alignment.Start),
    )
    {
        ResIcon(iconResId = iconResId)
        Text(
            label,
            style = MaterialTheme.typography.h6,
        )
    }
}