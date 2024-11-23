package com.tored.bridgelauncher.ui2.devconsole.composables

import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tored.bridgelauncher.R
import com.tored.bridgelauncher.composables.ResIcon
import com.tored.bridgelauncher.ui.theme.BridgeLauncherThemeStateless
import com.tored.bridgelauncher.ui2.devconsole.DevConsoleActions
import com.tored.bridgelauncher.ui2.devconsole.DevConsoleVM
import com.tored.bridgelauncher.ui2.devconsole.IConsoleMessage
import com.tored.bridgelauncher.ui2.devconsole.TestConsoleMessages
import com.tored.bridgelauncher.ui2.devconsole.getSourceAndLineString
import com.tored.bridgelauncher.ui2.shared.BotBarScreen
import com.tored.bridgelauncher.utils.UseEdgeToEdgeWithTransparentBars

@Composable
fun DevConsoleScreen(
    vm: DevConsoleVM = viewModel(factory = DevConsoleVM.Factory),
    requestFinish: () -> Unit,
)
{
    DevConsoleScreen(
        messages = vm.messages,
        actions = vm.actions,
        requestFinish = requestFinish,
    )
}

@Composable
fun DevConsoleScreen(
    messages: List<IConsoleMessage>,
    actions: DevConsoleActions,
    requestFinish: () -> Unit,
)
{
    UseEdgeToEdgeWithTransparentBars()

    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize(),
    )
    {
        BotBarScreen(
            onLeftActionClick = { requestFinish() },
            titleAreaContent = {
                Text(text = "Developer Console")
            },
            rightContent = {
                IconButton(
                    onClick = { actions.clearMessages() },
                )
                {
                    ResIcon(R.drawable.ic_clear_all)
                }
            }
        )
        {
            val context = LocalContext.current
            val clipman = LocalClipboardManager.current

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(0.dp, 8.dp),
                reverseLayout = true,
            )
            {
                items(messages.reversed(), key = { it.uid })
                { msg ->

                    ConsoleMessageListItem(
                        msg = msg,
                        click = {
                            clipman.setText(buildAnnotatedString {
                                appendLine(msg.getSourceAndLineString())
                                append(msg.message)
                            })
                            Toast.makeText(context, "Message copied to clipboard.", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}


// PREVIEWS

@PreviewLightDark
@Composable
fun DevConsoleScreenPreview()
{
    BridgeLauncherThemeStateless {
        DevConsoleScreen(
            messages = TestConsoleMessages.List,
            actions = DevConsoleActions.empty(),
            requestFinish = {},
        )
    }
}