package com.tored.bridgelauncher

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tored.bridgelauncher.ui.theme.BridgeLauncherTheme
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.tored.bridgelauncher.composables.Btn
import com.tored.bridgelauncher.composables.ResIcon
import com.tored.bridgelauncher.ui.theme.borders
import com.tored.bridgelauncher.ui.theme.checkedItemBg
import com.tored.bridgelauncher.ui.theme.textSec
import kotlinx.coroutines.flow.update
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.instanceParameter

data class SettingsUIState(

    val theme: ThemeOptions = ThemeOptions.System,

    val currentProjName: String = "Test Launcher",

    @Display("Allow projects to turn the screen off")
    val allowProjectsToTurnScreenOff: Boolean = false,

    @Display("Draw system wallpaper behind WebView")
    val drawSystemWallpaperBehindWebView: Boolean = true,

    @Display("Status bar")
    val statusBarAppearance: SystemBarAppearanceOptions = SystemBarAppearanceOptions.DarkIcons,

    @Display("Navigation bar")
    val navigationBarAppearance: SystemBarAppearanceOptions = SystemBarAppearanceOptions.DarkIcons,

    @Display("Draw WebView overscroll effects")
    val drawWebViewOverscrollEffects: Boolean = false,

    @Display("Show Bridge button")
    val showBridgeButton: Boolean = true,

    @Display("Show Launch apps button when the Bridge menu is collapsed")
    val showLaunchAppsWhenBridgeButtonCollapsed: Boolean = false,
)

@Target(AnnotationTarget.PROPERTY)
annotation class Display(val name: String)

class SettingsVM : ViewModel()
{
    val settingsUIState = MutableStateFlow(SettingsUIState())
}

class SettingsActivity : ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        setContent {
            BridgeLauncherTheme() {
                SettingsScreen()
            }
        }
    }
}

fun <TProp> displayNameFor(prop: KProperty1<SettingsUIState, TProp>): String
{
    val ann = prop.findAnnotation<Display>()
    return ann?.name ?: prop.name
}

@Composable
fun SettingsScreen(
    viewModel: SettingsVM = viewModel()
)
{
    val uiState by viewModel.settingsUIState.collectAsStateWithLifecycle()

    @Composable
    fun checkboxFieldFor(prop: KProperty1<SettingsUIState, Boolean>)
    {
        val instanceParam = SettingsUIState::copy.instanceParameter
            ?: throw Exception("Instance parameter not found.")
        val param = SettingsUIState::copy.parameters.find { it.name == prop.name }
            ?: throw Exception("Parameter ${prop.name} not found.")

        CheckboxField(
            label = displayNameFor(prop),
            isChecked = prop.getValue(uiState, prop),
            onCheckedChange = { isChecked ->
                viewModel.settingsUIState.update {
                    SettingsUIState::copy.callBy(
                        mapOf(
                            instanceParam to it,
                            param to isChecked
                        )
                    )
                }
            }
        )
    }

    @Composable
    fun systemBarOptionsFieldFor(prop: KProperty1<SettingsUIState, SystemBarAppearanceOptions>)
    {
        val instanceParam = SettingsUIState::copy.instanceParameter
            ?: throw Exception("Instance parameter not found.")
        val param = SettingsUIState::copy.parameters.find { it.name == prop.name }
            ?: throw Exception("Parameter ${prop.name} not found.")

        SystemBarAppearanceOptionsField(
            label = displayNameFor(prop),
            selectedOption = prop.getValue(uiState, prop),
            onChange = { value ->
                viewModel.settingsUIState.update {
                    SettingsUIState::copy.callBy(
                        mapOf(
                            instanceParam to it,
                            param to value
                        )
                    )
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
                ?: throw Exception("Could not access insets controller.")

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

    Column() {

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
                checkboxFieldFor(SettingsUIState::allowProjectsToTurnScreenOff)
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

                    checkboxFieldFor(SettingsUIState::drawSystemWallpaperBehindWebView)
                }
            }

            Divider()

            SettingsSection(label = "Overlays", iconResId = R.drawable.ic_overlays)
            {
                systemBarOptionsFieldFor(SettingsUIState::statusBarAppearance)
                systemBarOptionsFieldFor(SettingsUIState::navigationBarAppearance)
                checkboxFieldFor(SettingsUIState::drawWebViewOverscrollEffects)
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
                    onChange = {
                        theme ->
                        viewModel.settingsUIState.update {
                            it.copy(theme = theme)
                        }
                    }
                )

                checkboxFieldFor(SettingsUIState::showBridgeButton)

                ProvideTextStyle(value = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.textSec))
                {
                    Tip("Tap and hold the button to move it.")
                }

                checkboxFieldFor(SettingsUIState::showLaunchAppsWhenBridgeButtonCollapsed)

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

        Surface(
            modifier = Modifier
                .height(56.dp),
            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
            elevation = 4.dp,
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
fun Tip(text: String)
{
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    )
    {
        ResIcon(R.drawable.ic_tip, inline = true)
        Text(text)
    }
}

@Composable
fun CheckboxField(label: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit)
{
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(8.dp),
        color = if (isChecked)
            MaterialTheme.colors.checkedItemBg
        else
            Color.Transparent
    )
    {
        Row(
            modifier = Modifier
                .clickable { onCheckedChange(!isChecked) }
                .defaultMinSize(minHeight = 48.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(12.dp, 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        )
        {
            Checkbox(
                checked = isChecked,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colors.onSurface,
                    uncheckedColor = MaterialTheme.colors.onSurface,
                )
            )
            Text(label)
        }
    }
}

enum class SystemBarAppearanceOptions
{
    Hide,
    LightIcons,
    DarkIcons,
}

enum class ThemeOptions
{
    System,
    Light,
    Dark,
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
fun <TOption> OptionsRow(label: String, options: Map<TOption, String>, selectedOption: TOption, onChange: (TOption) -> Unit)
{
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    )
    {
        Text(label, modifier = Modifier.padding(4.dp, 0.dp))

        Row(
            modifier = Modifier
                .border(MaterialTheme.borders.soft, RoundedCornerShape(9.dp))
                .padding(1.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
        )
        {

            for (entry in options)
            {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 48.dp),
                    color = if (selectedOption == entry.key)
                        MaterialTheme.colors.checkedItemBg
                    else
                        Color.Transparent,
                    shape = RoundedCornerShape(8.dp),
                )
                {
                    Box(
                        modifier = Modifier
                            .clickable { onChange(entry.key) },
                        contentAlignment = Alignment.Center,
                    )
                    {
                        Text(entry.value)
                    }
                }
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
fun SettingsPreview()
{
    BridgeLauncherTheme {
        SystemBarAppearanceOptionsField("Status bar", SystemBarAppearanceOptions.Hide, { })
    }
}