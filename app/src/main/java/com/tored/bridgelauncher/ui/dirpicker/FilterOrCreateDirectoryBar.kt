package com.tored.bridgelauncher.ui.dirpicker

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.R
import com.tored.bridgelauncher.composables.ResIcon
import com.tored.bridgelauncher.ui.shared.TextFieldPlaceholderDecorationBox
import com.tored.bridgelauncher.ui.theme.BridgeLauncherThemeStateless

@Composable
fun DirPickerFilterOrCreateDirBar(
    text: String,
    onTextChange: (String) -> Unit,
    onCreateDirectoryClick: () -> Unit,
    modifier: Modifier = Modifier,
)
{
    Row(
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .height(IntrinsicSize.Min)
            .padding(end = 8.dp)
    )
    {
        BasicTextField(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            value = text,
            onValueChange = onTextChange,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                autoCorrect = false,
                keyboardType = KeyboardType.Uri,
                capitalization = KeyboardCapitalization.None,
            ),
            textStyle = TextStyle(
                platformStyle = PlatformTextStyle(includeFontPadding = true),
                color = MaterialTheme.colors.onSurface,
            ),
            cursorBrush = SolidColor(MaterialTheme.colors.primary),
            singleLine = true,
            decorationBox = { innerTextField ->
                TextFieldPlaceholderDecorationBox(
                    text = text,
                    placeholderText = "Filter or create directory...",
                    innerTextField = innerTextField
                )
            }
        )

        IconButton(
            onClick = onCreateDirectoryClick
        )
        {
            ResIcon(iconResId = R.drawable.ic_folder_plus)
        }
    }
}

@Composable
@Preview
fun DirPickerFilterOrCreateDirBarPreview()
{
    BridgeLauncherThemeStateless(useDarkTheme = false)
    {
        DirPickerFilterOrCreateDirBar(
            text = "",
            modifier = Modifier
                .fillMaxWidth(),
            onTextChange = {},
            onCreateDirectoryClick = {},
        )
    }
}