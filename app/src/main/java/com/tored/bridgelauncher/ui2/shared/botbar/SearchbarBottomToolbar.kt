package com.tored.bridgelauncher.ui2.shared.botbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.R
import com.tored.bridgelauncher.ui2.shared.ResIcon
import com.tored.bridgelauncher.ui2.shared.PreviewWithSurfaceAndPadding
import com.tored.bridgelauncher.ui2.shared.TextFieldPlaceholderDecorationBox
import com.tored.bridgelauncher.ui2.theme.inputFieldBg
import com.tored.bridgelauncher.utils.ComposableContent


@Composable
fun SearchbarBottomToolbar(
    modifier: Modifier = Modifier,
    leftActionIconResId: Int = R.drawable.ic_arrow_left,
    onLeftActionClick: () -> Unit,
    searchbarText: String,
    searchbarPlaceholderText: String,
    onSearchbarTextUpdateRequest: (newText: String) -> Unit,
    rightContent: ComposableContent? = null,
)
{
    EmptyBottomToolbar(
        modifier = modifier
    )
    {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(4.dp)
        )
        {
            IconButton(
                onClick = onLeftActionClick,
            )
            {
                ResIcon(leftActionIconResId)
            }


            Row(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colors.inputFieldBg,
                        shape = RoundedCornerShape(12.dp),
                    )
                    .weight(1f)
                    .height(48.dp),
            )
            {
                BasicTextField(
                    modifier = Modifier
                        .weight(1f),
                    value = searchbarText,
                    onValueChange = onSearchbarTextUpdateRequest,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.None,
                    ),
                    textStyle = MaterialTheme.typography.body1.copy(
                        platformStyle = PlatformTextStyle(includeFontPadding = true),
                        color = MaterialTheme.colors.onSurface,
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colors.primary),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        TextFieldPlaceholderDecorationBox(
                            text = searchbarText,
                            placeholderText = searchbarPlaceholderText,
                            innerTextField = innerTextField
                        )
                    }
                )

                if (searchbarText.isNotBlank())
                {
                    IconButton(
                        onClick = { onSearchbarTextUpdateRequest("") }
                    )
                    {
                        ResIcon(iconResId = R.drawable.ic_close)
                    }
                }
            }

            if (rightContent != null)
            {
                rightContent()
            }
        }
    }
}


// PREVIEWS

@Composable
fun SearchbarBottomToolbarPreview(
    text: String = "",
    placeholder: String = "Search apps...",
)
{
    PreviewWithSurfaceAndPadding {
        SearchbarBottomToolbar(
            onLeftActionClick = {},
            searchbarText = text,
            searchbarPlaceholderText = placeholder,
            onSearchbarTextUpdateRequest = {}
        )
    }
}

@Composable
@PreviewLightDark
fun SearchbarBottomToolbarPreview_Placeholder()
{
    SearchbarBottomToolbarPreview()
}

@Composable
@PreviewLightDark
fun SearchbarBottomToolbarPreview_WithText()
{
    SearchbarBottomToolbarPreview(text = "bridgelauncher")
}