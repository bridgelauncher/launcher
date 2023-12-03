package com.tored.bridgelauncher.ui.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.ui.theme.textPlaceholder
import com.tored.bridgelauncher.utils.ComposableContent

@Composable
fun TextFieldPlaceholderDecorationBox(text: String, placeholderText: String, innerTextField: ComposableContent)
{
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart,
    )
    {
        if (text.isEmpty())
            Text(placeholderText, color = MaterialTheme.colors.textPlaceholder)

        innerTextField()
    }
}