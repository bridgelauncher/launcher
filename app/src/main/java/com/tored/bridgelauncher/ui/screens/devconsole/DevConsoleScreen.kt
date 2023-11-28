package com.tored.bridgelauncher.ui.screens.devconsole

import android.annotation.SuppressLint
import android.webkit.ConsoleMessage
import android.webkit.ConsoleMessage.MessageLevel
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.BridgeLauncherApp
import com.tored.bridgelauncher.R
import com.tored.bridgelauncher.composables.ResIcon
import com.tored.bridgelauncher.ui.screens.home.BRIDGE_PROJECT_URL
import com.tored.bridgelauncher.ui.shared.SetSystemBarsForBotBarActivity
import com.tored.bridgelauncher.ui.theme.BridgeLauncherThemeStateless
import com.tored.bridgelauncher.ui.theme.info
import com.tored.bridgelauncher.ui.theme.textSec
import com.tored.bridgelauncher.ui.theme.warning

@Composable
fun DevConsoleScreenStateless(
    messages: List<ConsoleMessage>,
    onClearAllRequest: () -> Unit,
    onMessageClick: (ConsoleMessage) -> Unit,
)
{
    Surface(
        color = MaterialTheme.colors.background,
    )
    {
        Column()
        {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(0.dp, 8.dp),
                reverseLayout = true,
            )
            {
                if (messages.any())
                {
                    items(messages.reversed())
                    { msg ->
                        Surface(
                            color = when (msg.messageLevel())
                            {
                                MessageLevel.WARNING -> MaterialTheme.colors.warning.copy(alpha = 0.15f)
                                MessageLevel.ERROR -> MaterialTheme.colors.error.copy(alpha = 0.15f)
                                else -> Color.Transparent
                            }
                        )
                        {

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onMessageClick(msg) }
                                    .padding(8.dp, 8.dp),
                            )
                            {
                                val t = buildAnnotatedString()
                                {
                                    when (msg.messageLevel())
                                    {
                                        MessageLevel.TIP -> appendSecColored("TIP", MaterialTheme.colors.info)
                                        MessageLevel.DEBUG -> appendSecColored("DBG", MaterialTheme.colors.info)
                                        MessageLevel.WARNING -> appendSecColored("WARN", MaterialTheme.colors.warning)
                                        MessageLevel.ERROR -> appendSecColored("ERR", MaterialTheme.colors.error)
                                        else -> Unit
                                    }

                                    append(" ")
                                    append(msg.message())
                                }

                                Text(
                                    text = msg.getSourceAndLine(),
                                    style = MaterialTheme.typography.body2,
                                    color = MaterialTheme.colors.textSec,
                                )

                                Text(text = t)
                            }
                        }
                    }
                }
                else
                {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        )
                        {
                            ResIcon(R.drawable.ic_info, color = MaterialTheme.colors.textSec)
                            Text("No console messages to show.", color = MaterialTheme.colors.textSec)
                        }
                    }
                }
            }

            DevConsoleBotBar(
                modifier = Modifier
                    .fillMaxWidth(),
                onClearAllRequest = onClearAllRequest,
            )
        }
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun AnnotatedString.Builder.appendSecColored(text: String, color: Color)
{
    append(AnnotatedString(text, SpanStyle(color = color, fontSize = MaterialTheme.typography.body2.fontSize)))
}

fun ConsoleMessage.getSourceAndLine(): String
{
    var source = sourceId()

    if (source.startsWith(BRIDGE_PROJECT_URL))
        source = source.substringAfter(BRIDGE_PROJECT_URL)

    return "${source}:${lineNumber()}"
}


@Composable
fun DevConsoleScreenStateful()
{
    val context = LocalContext.current
    val app = context.applicationContext as BridgeLauncherApp
    val clipman = LocalClipboardManager.current

    SetSystemBarsForBotBarActivity()

    DevConsoleScreenStateless(
        messages = app.consoleMessagesHolder.messages,
        onClearAllRequest = {
            app.consoleMessagesHolder.messages.clear()
        },
        onMessageClick = {
            clipman.setText(buildAnnotatedString {
                appendLine(it.getSourceAndLine())
                append(it.message())
            })
            Toast.makeText(context, "Message copied to clipboard.", Toast.LENGTH_SHORT).show()
        }
    )
}

@Composable
@Preview
fun DevConsoleScreenPreview()
{
    BridgeLauncherThemeStateless(useDarkTheme = true)
    {
        DevConsoleScreenStateless(
            messages = listOf(
                ConsoleMessage("I have information that could lead to the arrest of Hillary Clinton", "LOL", 10, MessageLevel.LOG),
                ConsoleMessage("Debug message!", "LOL", 10, MessageLevel.DEBUG),
                ConsoleMessage("Watch for rolling rocks", "LOL", 123, MessageLevel.TIP),
                ConsoleMessage("Caution! Wet floor.", "LOL", 5124, MessageLevel.WARNING),
                ConsoleMessage("Major malfunction.", "LOL", 5124, MessageLevel.ERROR)
            ),
            onClearAllRequest = {},
            onMessageClick = {}
        )
    }
}

@Composable
@Preview
fun DevConsoleScreenNoMessagesPreview()
{
    BridgeLauncherThemeStateless(useDarkTheme = false)
    {
        DevConsoleScreenStateless(
            messages = listOf(),
            onClearAllRequest = {},
            onMessageClick = {}
        )
    }
}