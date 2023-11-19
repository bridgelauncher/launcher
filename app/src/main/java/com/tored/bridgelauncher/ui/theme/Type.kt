package com.tored.bridgelauncher.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tored.bridgelauncher.R

val FrancoisOne = FontFamily(
    Font(R.font.francois_one),
)

val Signika = FontFamily(
    Font(R.font.signika),
    Font(R.font.signika_bold, FontWeight.Bold),
)

// Set of Material typography styles to start with
@Suppress("DEPRECATION")
@OptIn(ExperimentalTextApi::class)
val Typography = Typography(
    defaultFontFamily = Signika,

    h6 = TextStyle(
        fontFamily = FrancoisOne,
        fontSize = 20.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        ),
    ),

    body1 = TextStyle(
        fontFamily = Signika,
        fontSize = 16.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        ),
    ),

    body2 = TextStyle(
        fontFamily = Signika,
        fontSize = 12.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        ),
    ),
)

@Preview
@Composable
fun TypePreview()
{
    BridgeLauncherTheme()
    {
        Box(
            modifier = Modifier.height(48.dp),
            contentAlignment = Alignment.Center
        )
        {
            Text(
                "Test text",
                style = MaterialTheme.typography.h6
            )
        }
    }
}