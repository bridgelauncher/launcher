package com.tored.bridgelauncher.ui.dirpicker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.R
import com.tored.bridgelauncher.composables.ResIcon
import com.tored.bridgelauncher.ui.theme.textSec

@Composable
fun DirPickerCurrentDirContent(
    uiState: DirPickerUIState.HasPermission,
    modifier: Modifier = Modifier,
    onNavigateRequest: (Directory) -> Unit
)
{
    if (uiState.hasNothingtoShow)
    {
        DirPickerNothingToShow(
            modifier = modifier,
            text = if (uiState.isDirEmpty) "This directory is empty." else "Nothing matched the filter."
        )
    }
    else
    {
        LazyColumn(
            modifier = modifier
        )
        {
            items(uiState.startupSubfiles)
            { file ->
                DirPickerStartupSubfileListItem(file = file)
            }

            items(uiState.subdirs)
            { dir ->
                DirPickerSubdirListItem(dir = dir)
                {
                    onNavigateRequest(dir)
                }
            }

            items(uiState.regularSubfiles)
            { file ->
                DirPickerSubfileListItem(file = file)
            }
        }
    }
}

@Composable
fun DirPickerNothingToShow(
    text: String,
    modifier: Modifier = Modifier,
)
{
    Column(
        modifier = modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.CenterVertically),
    )
    {
        ResIcon(R.drawable.ic_folder_search, color = MaterialTheme.colors.textSec)
        Text(text, color = MaterialTheme.colors.textSec)
    }
}