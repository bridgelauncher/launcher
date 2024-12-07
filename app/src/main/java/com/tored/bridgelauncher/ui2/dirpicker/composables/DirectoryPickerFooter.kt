package com.tored.bridgelauncher.ui2.dirpicker.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.ui2.shared.Btn

@Composable
fun DirectoryPickerFooter(
    confirmText: String,
    confirmIconResId: Int,
    cancelText: String,
    isConfirmDisabled: Boolean,
    onCancelRequest: () -> Unit,
    onConfirmRequest: () -> Unit,
    modifier: Modifier = Modifier
)
{
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.End)
    )
    {
        Btn(
            text = cancelText,
            contentColor = MaterialTheme.colors.onSurface,
            onClick = onCancelRequest,
        )

        Btn(
            text = confirmText,
            suffixIcon = confirmIconResId,
            disabled = isConfirmDisabled,
            onClick = onConfirmRequest
        )
    }
}