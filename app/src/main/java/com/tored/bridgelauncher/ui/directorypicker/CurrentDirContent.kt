package com.tored.bridgelauncher.ui.directorypicker

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
fun DirectoryPickerCurrentDirContent(
    uiState: DirectoryPickerUIState.HasPermission,
    modifier: Modifier = Modifier,
    onNavigateRequest: (Directory) -> Unit
)
{
    if (uiState.isCurrentDirEmpty)
    {
        DirectoryPickerEmptyDirectory(
            modifier = modifier
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
                DirectoryPickerStartupSubfileListItem(file = file)
            }

            items(uiState.subdirs)
            { dir ->
                DirectoryPickerSubdirListItem(dir = dir)
                {
                    onNavigateRequest(dir)
                }
            }

            items(uiState.regularSubfiles)
            { file ->
                DirectoryPickerSubfileListItem(file = file)
            }
        }
    }
}

@Composable
fun DirectoryPickerEmptyDirectory(
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
        Text("This directory is empty.", color = MaterialTheme.colors.textSec)
    }
}