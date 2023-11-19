package com.tored.bridgelauncher.ui.directorypicker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.R
import com.tored.bridgelauncher.composables.Btn

@Composable
fun DirectoryPickerFooter(
    isConfirmDisabled: Boolean,
    onCancelRequest: () -> Unit,
    onConfirmRequest: () -> Unit,
    modifier: Modifier = Modifier
)
{
    Row(
        modifier = modifier
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.End)
    )
    {
        Btn(
            text = "Cancel",
            color = MaterialTheme.colors.onSurface,
            onClick = onCancelRequest,
        )

        Btn(
            text = "Use this directory",
            suffixIcon = R.drawable.ic_check,
            disabled = isConfirmDisabled,
            onClick = onConfirmRequest
        )
    }
}