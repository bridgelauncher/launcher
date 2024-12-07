package com.tored.bridgelauncher.ui2.appdrawer.composables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tored.bridgelauncher.ui2.appdrawer.AppDrawerVM
import com.tored.bridgelauncher.ui2.appdrawer.IAppDrawerApp
import com.tored.bridgelauncher.ui2.appdrawer.TestApps
import com.tored.bridgelauncher.ui2.appdrawer.composables.appcontextmenu.AppContextMenu
import com.tored.bridgelauncher.ui2.appdrawer.composables.appcontextmenu.AppContextMenuState
import com.tored.bridgelauncher.ui2.shared.BotBarScreen
import com.tored.bridgelauncher.ui2.shared.botbar.SearchbarBottomToolbar
import com.tored.bridgelauncher.ui2.theme.BridgeLauncherThemeStateless
import com.tored.bridgelauncher.utils.UseEdgeToEdgeWithTransparentBars
import com.tored.bridgelauncher.utils.launchApp

@Composable
fun AppDrawerScreen(
    vm: AppDrawerVM = viewModel(factory = AppDrawerVM.Factory),
    requestFinish: () -> Unit,
)
{
    AppDrawerScreen(
        filteredApps = vm.filteredApps.value,
        searchString = vm.searchString.value,
        updateSearchStringRequest = { vm.updateSearchStringRequest(it) },
        getIconFunc = { iconPack, app ->
            vm.getIcon(iconPack, app)
        },
        requestFinish = requestFinish,
    )
}

@Composable
fun AppDrawerScreen(
    filteredApps: List<IAppDrawerApp>,
    searchString: String,
    updateSearchStringRequest: (newString: String) -> Unit,
    getIconFunc: AppIconGetIconFunc,
    requestFinish: () -> Unit,
)
{
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current

    var appContextMenuState by remember { mutableStateOf<AppContextMenuState?>(null) }

    var dropdownParentSize by remember { mutableStateOf(IntSize(0, 0)) }

    UseEdgeToEdgeWithTransparentBars()

    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        BotBarScreen(
            botBar = {
                SearchbarBottomToolbar(
                    onLeftActionClick = { requestFinish() },
                    searchbarText = searchString,
                    searchbarPlaceholderText = "Search apps...",
                    onSearchbarTextUpdateRequest = updateSearchStringRequest,
                )
            }
        )
        {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned {
                        dropdownParentSize = it.size
                    },
                reverseLayout = true,
                contentPadding = PaddingValues(0.dp, 8.dp),
            )
            {
                items(filteredApps, key = { it.packageName })
                { app ->

                    var dropdownItemInLazyColOffset by remember { mutableStateOf(Offset(0f, 0f)) }

                    AppListItem(
                        app = app,
                        getIconFunc = getIconFunc,
                        onTap = { context.launchApp(app.packageName) },
                        onLongPress = { offset ->

                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)

                            var dropdownFinalOffset = (dropdownItemInLazyColOffset + offset).round()
                            val dropToLeft = dropdownFinalOffset.x > dropdownParentSize.width / 2
                            val dropUp = dropdownFinalOffset.y > dropdownParentSize.height / 2

                            val dropdownAlignment = if (dropToLeft)
                            {
                                if (dropUp)
                                    Alignment.BottomEnd
                                else
                                    Alignment.TopEnd
                            }
                            else
                            {
                                if (dropUp)
                                    Alignment.BottomStart
                                else
                                    Alignment.TopStart
                            }

                            if (dropToLeft)
                                dropdownFinalOffset = dropdownFinalOffset.copy(x = dropdownFinalOffset.x - dropdownParentSize.width)

                            if (dropUp)
                                dropdownFinalOffset = dropdownFinalOffset.copy(y = dropdownFinalOffset.y - dropdownParentSize.height)

                            appContextMenuState = AppContextMenuState(
                                app = app,
                                offset = dropdownFinalOffset,
                                alignment = dropdownAlignment,
                            )
                        },
                        modifier = Modifier.onGloballyPositioned {
                            dropdownItemInLazyColOffset = it.positionInParent()
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                }
            }

            val state = appContextMenuState
            if (state != null)
            {
                AppContextMenu(
                    state = state,
                    onDismissRequest = { appContextMenuState = null },
                )
            }
        }
    }
}


// PREVIEWS

@Composable
fun AppDrawerScreenPreview(
    filteredApps: List<IAppDrawerApp> = TestApps.List,
)
{
    BridgeLauncherThemeStateless {
        AppDrawerScreen(
            filteredApps = filteredApps,
            getIconFunc = emptyGetIconFunc,
            searchString = "",
            updateSearchStringRequest = {},
            requestFinish = { },
        )
    }
}

@PreviewLightDark
@Composable
fun AppDrawerScreenPreview_01()
{
    AppDrawerScreenPreview()
}