package com.tored.bridgelauncher.ui2.progressdialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.tored.bridgelauncher.R
import com.tored.bridgelauncher.ui2.shared.Btn
import com.tored.bridgelauncher.services.mockexport.MockExportProgressState
import com.tored.bridgelauncher.services.mockexport.hasFinished
import com.tored.bridgelauncher.ui2.theme.borderLight
import com.tored.bridgelauncher.ui2.shared.PreviewWithSurfaceAndPadding

@Composable
fun MockExportProgressDialog(
    state: MockExportProgressState,
    actions: MockExportProgressDialogActions,
)
{
    Dialog(
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
        onDismissRequest = { actions.dismiss() },
    )
    {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        )
        {
            MockExportProgressDialogContent(
                state = state,
                actions = actions,
            )
        }
    }
}

@Composable
fun MockExportProgressDialogContent(
    state: MockExportProgressState,
    actions: MockExportProgressDialogActions,
)
{
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {}, interactionSource = remember { MutableInteractionSource() }, indication = null),
        color = MaterialTheme.colors.background,
        shape = MaterialTheme.shapes.large,
        elevation = 8.dp,
    )
    {
        Column()
        {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .defaultMinSize(minHeight = 56.dp)
//                    .wrapContentHeight()
//                    .padding(16.dp, 8.dp),
//                verticalAlignment = Alignment.CenterVertically,
//            )
//            {
//                Text("Export progress", style = MaterialTheme.typography.h6)
//            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 16.dp, 16.dp, 8.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            )
            {
                if (state.hasFinished)
                    Text("Export finished!")
                else
                    Text("Exporting, please wait...")

                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                )
                {
                    val remainingJobs = state.jobsToDo - state.jobsFailed - state.jobsDone
                    val donePercent = (state.jobsDone + state.jobsFailed) / state.jobsToDo.toFloat() * 100

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                    )
                    {
                        if (state.jobsFailed > 0)
                            Box(
                                modifier = Modifier
                                    .weight(state.jobsFailed.toFloat())
                                    .fillMaxHeight()
                                    .background(MaterialTheme.colors.error)
                            )

                        if (state.jobsDone > 0)
                            Box(
                                modifier = Modifier
                                    .weight(state.jobsDone.toFloat())
                                    .fillMaxHeight()
                                    .background(MaterialTheme.colors.primary)
                            )

                        if (state.jobsToDo == 0 || remainingJobs > 0)
                            Box(
                                modifier = Modifier
                                    .weight(if (state.jobsToDo == 0) 1f else remainingJobs.toFloat())
                                    .fillMaxHeight()
                                    .background(MaterialTheme.colors.borderLight)
                            )
                    }

                    Row(

                    )
                    {
                        CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.body2)
                        {
                            if (state.jobsFailed > 0)
                                Text("${state.jobsFailed} failed", color = MaterialTheme.colors.error)
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                buildAnnotatedString {
                                    withStyle(style = SpanStyle(color = MaterialTheme.colors.primary))
                                    {
                                        append(state.jobsDone.toString())
                                    }
                                    append("/${state.jobsToDo} (${donePercent.toInt()}%)")
                                }
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.End)
            )
            {
                if (state.hasFinished)
                {
                    Btn(
                        text = "Ok",
                        suffixIcon = R.drawable.ic_check,
                        contentColor = MaterialTheme.colors.primary,
                        onClick = { TODO() },
                    )
                }
                else
                {
                    Btn(
                        text = "Cancel",
                        suffixIcon = R.drawable.ic_close,
                        contentColor = MaterialTheme.colors.onSurface,
                        onClick = { TODO() },
                    )
                }
            }
        }
    }
}

// PREVIEWS

@Composable
fun MockExportProgressDialogPreview(jobsToDo: Int, jobsDone: Int, jobsFailed: Int)
{
    PreviewWithSurfaceAndPadding {
        MockExportProgressDialogContent(
            state = MockExportProgressState(
                jobsToDo = jobsToDo,
                jobsDone = jobsDone,
                jobsFailed = jobsFailed
            ),
            actions = MockExportProgressDialogActions.empty(),
        )
    }
}

@Composable
@PreviewLightDark
fun MockExportProgressDialogPreview01_Empty()
{
    MockExportProgressDialogPreview(
        jobsToDo = 0,
        jobsDone = 0,
        jobsFailed = 0,
    )
}

@Composable
@PreviewLightDark
fun MockExportProgressDialogPreview02_InProgress()
{
    MockExportProgressDialogPreview(
        jobsToDo = 100,
        jobsDone = 50,
        jobsFailed = 10,
    )
}

@Composable
@PreviewLightDark
fun MockExportProgressDialogPreview03_Finished()
{
    MockExportProgressDialogPreview(
        jobsToDo = 100,
        jobsDone = 90,
        jobsFailed = 10,
    )
}