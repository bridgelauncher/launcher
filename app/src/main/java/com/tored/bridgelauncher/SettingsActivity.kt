package com.tored.bridgelauncher

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tored.bridgelauncher.ui.theme.BridgeLauncherTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.datastore.preferences.preferencesDataStore
import com.tored.bridgelauncher.annotations.Display
import com.tored.bridgelauncher.composables.Btn
import com.tored.bridgelauncher.composables.ResIcon
import com.tored.bridgelauncher.composables.Tip
import com.tored.bridgelauncher.ui.shared.CheckboxField
import com.tored.bridgelauncher.ui.shared.OptionsRow
import com.tored.bridgelauncher.ui.theme.borders
import com.tored.bridgelauncher.ui.theme.textSec
import com.tored.bridgelauncher.utils.RawRepresentable
import com.tored.bridgelauncher.vms.SettingsState
import com.tored.bridgelauncher.vms.SettingsVM
import com.tored.bridgelauncher.vms.writeBool
import com.tored.bridgelauncher.vms.writeEnum
import dagger.hilt.android.AndroidEntryPoint
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.instanceParameter

enum class SystemBarAppearanceOptions(override val rawValue: Int) : RawRepresentable<Int>
{
    Hide(0),
    LightIcons(1),
    DarkIcons(2),
}

enum class ThemeOptions(override val rawValue: Int) : RawRepresentable<Int>
{
    System(0),
    Light(1),
    Dark(2),
}

@AndroidEntryPoint
class SettingsActivity : ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        setContent {
            BridgeLauncherTheme()
            {
                SettingsScreen()
            }
        }
    }
}

fun <TProp> displayNameFor(prop: KProperty1<SettingsState, TProp>): String
{
    val ann = prop.findAnnotation<Display>()
    return ann?.name ?: prop.name
}

@Composable
fun SettingsScreen(vm: SettingsVM = viewModel())
{
    val uiState by vm.settingsUIState.collectAsStateWithLifecycle()
    LaunchedEffect(vm) { vm.request() }

    @Composable
    fun checkboxFieldFor(prop: KProperty1<SettingsState, Boolean>)
    {
        CheckboxField(
            label = displayNameFor(prop),
            isChecked = prop.getValue(uiState, prop),
            onCheckedChange = { isChecked ->
                vm.edit {
                    it.writeBool(prop, isChecked)
                }
            }
        )
    }

    @Composable
    fun systemBarOptionsFieldFor(prop: KProperty1<SettingsState, SystemBarAppearanceOptions>, vm: SettingsVM = viewModel())
    {
        SystemBarAppearanceOptionsField(
            label = displayNameFor(prop),
            selectedOption = prop.getValue(uiState, prop),
            onChange = { value ->
                vm.edit {
                    it.writeEnum(prop, value)
                }
            }
        )
    }

    val currentView = LocalView.current
    if (!currentView.isInEditMode)
    {
        val currentWindow = (currentView.context as? Activity)?.window
            ?: throw Exception("Attempt to access a window from outside an activity.")

        val surfaceColor = MaterialTheme.colors.surface.toArgb()
        val isLight = MaterialTheme.colors.isLight

        SideEffect()
        {
            val insetsController = WindowCompat.getInsetsController(currentWindow, currentView)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                currentWindow.isNavigationBarContrastEnforced = false
            }

            currentWindow.statusBarColor = surfaceColor
            currentWindow.navigationBarColor = surfaceColor
            insetsController.isAppearanceLightStatusBars = isLight
            insetsController.isAppearanceLightNavigationBars = isLight
        }
    }

    Surface(
        color = MaterialTheme.colors.background
    )
    {
        Column()
        {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(0.dp, 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            )
            {
                SettingsSection(label = "Project", iconResId = R.drawable.ic_open_folder)
                {
                    CurrentProjectCard(uiState.currentProjName) { }
                    checkboxFieldFor(SettingsState::allowProjectsToTurnScreenOff)
                }

                Divider()

                SettingsSection(label = "System wallpaper", iconResId = R.drawable.ic_image)
                {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    )
                    {
                        Btn(
                            modifier = Modifier
                                .fillMaxWidth(),
                            text = "Set system wallpaper",
                            outlined = true,
                            onClick = { /*TODO*/ },
                        )

                        checkboxFieldFor(SettingsState::drawSystemWallpaperBehindWebView)
                    }
                }

                Divider()

                SettingsSection(label = "Overlays", iconResId = R.drawable.ic_overlays)
                {
                    systemBarOptionsFieldFor(SettingsState::statusBarAppearance)
                    systemBarOptionsFieldFor(SettingsState::navigationBarAppearance)
                    checkboxFieldFor(SettingsState::drawWebViewOverscrollEffects)
                }

                Divider()

                SettingsSection(label = "Bridge", iconResId = R.drawable.ic_bridge)
                {
                    OptionsRow(
                        label = "Theme",
                        options = mapOf(
                            ThemeOptions.System to "System",
                            ThemeOptions.Light to "Light",
                            ThemeOptions.Dark to "Dark",
                        ),
                        selectedOption = uiState.theme,
                        onChange = { theme ->
                            vm.edit {
                                it.writeEnum(SettingsState::theme, theme)
                            }
                        },
                    )

                    checkboxFieldFor(SettingsState::showBridgeButton)

                    ProvideTextStyle(value = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.textSec))
                    {
                        Tip("Tap and hold the button to move it.")
                    }

                    checkboxFieldFor(SettingsState::showLaunchAppsWhenBridgeButtonCollapsed)

                    ActionCard(
                        title = "Quick settings tile",
                        description = "You can add a quick settings tile to unobtrusively toggle the Bridge button."
                    )
                    {
                        Btn(text = "Add tile", suffixIcon = R.drawable.ic_plus, onClick = { /* TODO */ })
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .border(MaterialTheme.borders.soft, RoundedCornerShape(8.dp))
                            .padding(start = 12.dp, top = 16.dp, bottom = 16.dp, end = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    )
                    {
                        ResIcon(R.drawable.ic_check, color = MaterialTheme.colors.primary)
                        Text("Quick settings tile added.")
                    }
                }

                Divider()

                SettingsSection(label = "Development", iconResId = R.drawable.ic_tools)
                {
                    ActionCard(
                        title = "Bridge developer hub",
                        description = "Documentation and tools to help you develop Bridge Launcher projects."
                    )
                    {
                        Btn(text = "Open in browser", suffixIcon = R.drawable.ic_open_in_new, onClick = { /* TODO */ })
                    }

                    ActionCard(
                        title = "Export installed apps",
                        description = "Create a folder with information about apps installed on this phone, including icons. You can use this folder to work on projects from your PC."
                    )
                    {
                        Btn(text = "Export", suffixIcon = R.drawable.ic_save_to_device, onClick = { /* TODO */ })
                    }
                }

                Divider()

                SettingsSection(label = "About & Contact", iconResId = R.drawable.ic_about)
                {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.End,
                    )
                    {
                        Btn(text = "GitHub repository", suffixIcon = R.drawable.ic_open_in_new, onClick = { /* TODO */ })
                        Btn(text = "Discord server", suffixIcon = R.drawable.ic_open_in_new, onClick = { /* TODO */ })
                        Btn(text = "Send me an email", suffixIcon = R.drawable.ic_arrow_right, onClick = { /* TODO */ })
                        Btn(text = "Copy my email address", suffixIcon = R.drawable.ic_copy, onClick = { /* TODO */ })
                    }
                }
            }

            SettingsBotBar()

        }
    }
}

@Composable
fun SettingsBotBar(modifier: Modifier = Modifier)
{
    Surface(
        color = MaterialTheme.colors.surface,
        modifier = modifier
            .height(56.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
//        elevation = 4.dp,
    )
    {
        Row(
            modifier = Modifier
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        )
        {
            val context = LocalContext.current as Activity

            IconButton(onClick = { context.finish() })
            {
                ResIcon(R.drawable.ic_arrow_left)
            }
            Text(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight(),
                text = "Settings",
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.size(48.dp))
//                IconToggleButton(
//                    checked = MaterialTheme.colors.isLight,
//                    onCheckedChange = { /* TODO */ }
//                )
//                {
//                    ResIcon(iconResId = R.drawable.ic_dark_mode)
//                }
        }
    }
}

typealias ComposableContent = @Composable () -> Unit

@Composable
fun SettingsSection(label: String, iconResId: Int, content: ComposableContent)
{
    Column(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth(),
    )
    {
        SettingsSectionHeader(label = label, iconResId = iconResId)
        Column(
            modifier = Modifier
                .padding(start = 16.dp, top = 8.dp, bottom = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        )
        {
            content()
        }
    }
}

@Composable
fun SettingsSectionHeader(label: String, iconResId: Int)
{
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .padding(20.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp, alignment = Alignment.Start),
    )
    {
        ResIcon(iconResId = iconResId)
        Text(
            label,
            style = MaterialTheme.typography.h6,
        )
    }
}

@Composable
fun CurrentProjectCard(currentProjName: String, onChangeClick: () -> Unit)
{
    Surface(
        modifier = Modifier
            .border(border = MaterialTheme.borders.soft, shape = RoundedCornerShape(8.dp))
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
    )
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        )
        {
            Column(
                modifier = Modifier.weight(1f)
            )
            {
                Text("Current project", style = MaterialTheme.typography.body2, color = MaterialTheme.colors.textSec)
                Text(currentProjName, style = MaterialTheme.typography.body1)
            }
            Btn(text = "Change", onClick = onChangeClick)
        }
    }
}

@Composable
fun ActionCard(title: String, description: String, footer: ComposableContent)
{
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .border(MaterialTheme.borders.soft, RoundedCornerShape(8.dp)),
    )
    {
        Column(
            modifier = Modifier
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 0.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        )
        {
            Text(title)
            Text(description, style = MaterialTheme.typography.body2, color = MaterialTheme.colors.textSec)
        }
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.spacedBy(4.dp, alignment = Alignment.End)
        )
        {
            footer()
        }
    }
}


@Composable
fun SystemBarAppearanceOptionsField(label: String, selectedOption: SystemBarAppearanceOptions, onChange: (SystemBarAppearanceOptions) -> Unit)
{
    OptionsRow(
        label = label,
        options = mapOf(
            SystemBarAppearanceOptions.Hide to "Hide",
            SystemBarAppearanceOptions.LightIcons to "Light icons",
            SystemBarAppearanceOptions.DarkIcons to "Dark icons",
        ),
        selectedOption = selectedOption,
        onChange = onChange
    )
}


@Composable
@Preview(showBackground = true)
fun SettingsPreview()
{
    BridgeLauncherTheme {
        SystemBarAppearanceOptionsField("Status bar", SystemBarAppearanceOptions.Hide, { })
    }
}