package com.tored.bridgelauncher.ui.shared

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.ui.theme.borders
import com.tored.bridgelauncher.ui.theme.textSec
import com.tored.bridgelauncher.utils.ComposableContent

@Composable
fun ActionCard(title: String, description: String, footerContent: ComposableContent? = null)
{
    ActionCard(
        title = title,
        descriptionParagraphs = listOf(description),
        footerContent = footerContent
    )
}

@Composable
fun ActionCard(
    title: String,
    descriptionParagraphs: Iterable<String>,
    footerContent: ComposableContent? = null
)
{
    val pad = 24.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .border(MaterialTheme.borders.soft, MaterialTheme.shapes.large),
    )
    {
        Column(
            modifier = Modifier
                .padding(
                    top = pad,
                    start = pad,
                    end = pad,
                    bottom = if (footerContent == null) pad else 8.dp
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        )
        {
            Text(title)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            )
            {
                for (paragraph in descriptionParagraphs)
                {
                    Text(paragraph, style = MaterialTheme.typography.body2, color = MaterialTheme.colors.textSec)
                }
            }
        }

        if (footerContent != null)
        {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.spacedBy(4.dp, alignment = Alignment.End)
            )
            {
                footerContent()
            }
        }
    }
}