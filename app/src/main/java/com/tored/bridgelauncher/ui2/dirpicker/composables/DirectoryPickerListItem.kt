package com.tored.bridgelauncher.ui2.DirectoryPicker.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.R
import com.tored.bridgelauncher.ui2.shared.ResIcon
import com.tored.bridgelauncher.ui2.dirpicker.DirectoryPickerDirectory
import com.tored.bridgelauncher.ui2.dirpicker.DirectoryPickerDummyDirectory
import com.tored.bridgelauncher.ui2.dirpicker.DirectoryPickerDummyFile
import com.tored.bridgelauncher.ui2.dirpicker.DirectoryPickerFile
import com.tored.bridgelauncher.ui2.shared.PreviewWithSurfaceAndPadding

@Composable
fun DirectoryPickerListItem(
    priIconResId: Int,
    priText: String,
    modifier: Modifier = Modifier,
    secText: String = "",
    showSuffixArrow: Boolean = false,
    color: Color = MaterialTheme.colors.onSurface,
    forceFullAlpha: Boolean = false,
    onClick: (() -> Unit)? = null,
)
{
    Row(
        modifier = modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .run {
                if (onClick == null)
                    this
                else
                    clickable { onClick() }
            }
            .padding(16.dp, 8.dp)
            .alpha(if (onClick == null && !forceFullAlpha) 0.4f else 1f),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    )
    {
        CompositionLocalProvider(LocalContentColor provides color)
        {
            ResIcon(priIconResId)

            Row(
                modifier = Modifier
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            )
            {

                Text(priText, color = LocalContentColor.current)

                if (secText.isNotEmpty())
                {
                    Text(
                        secText,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.alpha(0.6f),
                    )
                }
            }

            if (showSuffixArrow)
            {
                ResIcon(
                    R.drawable.ic_arrow_right_thin,
                    modifier = Modifier.alpha(0.4f),
                )
            }
        }
    }
}


@Composable
fun DirectoryPickerUpListItem(
    upDir: DirectoryPickerDirectory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
)
{
    DirectoryPickerListItem(
        modifier = modifier,
        priIconResId = R.drawable.ic_folder_up,
        priText = "Go up",
        secText = "to ${upDir.name}",
        onClick = onClick,
    )
}

@Composable
@PreviewLightDark
private fun DirectoryPickerUpListItemPreview()
{
    PreviewWithSurfaceAndPadding {
        DirectoryPickerUpListItem(
            DirectoryPickerDummyDirectory("dog", "/up/dog"),
            { }
        )
    }
}


@Composable
fun DirectoryPickerUpDisabledListItem(
    modifier: Modifier = Modifier,
)
{
    DirectoryPickerListItem(
        modifier = modifier,
        priIconResId = R.drawable.ic_folder_up,
        priText = "Can't go up"
    )
}

@Composable
@PreviewLightDark
private fun DirectoryPickerUpDisabledListItemPreview()
{
    PreviewWithSurfaceAndPadding {
        DirectoryPickerUpDisabledListItem()
    }
}


@Composable
fun DirectoryPickerSubdirListItem(
    dir: DirectoryPickerDirectory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
)
{
    DirectoryPickerListItem(
        modifier = modifier,
        priIconResId = R.drawable.ic_folder_open,
        priText = dir.name,
        secText = if(dir.canRead) "/" else "(no access)",
        showSuffixArrow = dir.canRead,
        onClick = if(dir.canRead) onClick else null,
    )
}

@Composable
@PreviewLightDark
private fun DirectoryPickerSubdirListItemPreview()
{
    PreviewWithSurfaceAndPadding {
        DirectoryPickerSubdirListItem(
            dir = DirectoryPickerDummyDirectory("dog", "/up/dog"),
            onClick = {},
        )
    }
}

@Composable
@PreviewLightDark
private fun DirectoryPickerSubdirCantReadListItemPreview()
{
    PreviewWithSurfaceAndPadding {
        DirectoryPickerSubdirListItem(
            dir = DirectoryPickerDummyDirectory("dog", "/up/dog", canRead = false),
            onClick = {},
        )
    }
}


@Composable
fun DirectoryPickerSubfileListItem(
    file: DirectoryPickerFile,
    modifier: Modifier = Modifier,
)
{
    DirectoryPickerListItem(
        priIconResId = R.drawable.ic_file,
        priText = file.name,
        secText = if(file.canRead) "" else "(no access)",
        modifier = modifier,
    )
}

@Composable
@PreviewLightDark
private fun DirectoryPickerSubfileListItemPreview()
{
    PreviewWithSurfaceAndPadding {
        DirectoryPickerSubfileListItem(
            file = DirectoryPickerDummyFile("dog.png", "/up/dog.png"),
        )
    }
}

@Composable
@PreviewLightDark
private fun DirectoryPickerSubfileCantReadListItemPreview()
{
    PreviewWithSurfaceAndPadding {
        DirectoryPickerSubfileListItem(
            file = DirectoryPickerDummyFile("dog.png", "/up/dog.png", canRead = false),
        )
    }
}


@Composable
fun DirectoryPickerStartupSubfileListItem(
    file: DirectoryPickerFile,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
)
{
    DirectoryPickerListItem(
        priIconResId = R.drawable.ic_file_star_filled,
        priText = file.name,
        secText = if(file.canRead) "Startup file" else "Startup file (no access)",
        color = MaterialTheme.colors.primary,
        forceFullAlpha = true,
        modifier = modifier,
        onClick = onClick,
    )
}

@Composable
@PreviewLightDark
private fun DirectoryPickerStartupSubfileListItemPreview()
{
    PreviewWithSurfaceAndPadding {
        DirectoryPickerStartupSubfileListItem(
            file = DirectoryPickerDummyFile("dog.png", "/up/dog.png"),
            onClick = {}
        )
    }
}

@Composable
@PreviewLightDark
private fun DirectoryPickerStartupSubfileCantReadListItemPreview()
{
    PreviewWithSurfaceAndPadding {
        DirectoryPickerStartupSubfileListItem(
            file = DirectoryPickerDummyFile("dog.png", "/up/dog.png", canRead = false),
            onClick = {}
        )
    }
}