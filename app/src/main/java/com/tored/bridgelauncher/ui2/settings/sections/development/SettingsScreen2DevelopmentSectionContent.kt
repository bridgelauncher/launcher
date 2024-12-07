package com.tored.bridgelauncher.ui2.settings.sections.development

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.tored.bridgelauncher.R
import com.tored.bridgelauncher.ui2.shared.ActionCard
import com.tored.bridgelauncher.ui2.shared.Btn
import com.tored.bridgelauncher.ui2.shared.PreviewWithSurfaceAndPadding

@Composable
fun SettingsScreen2DevelopmentSectionContent(
    state: SettingsScreen2DevelopmentSectionState,
    actions: SettingsScreen2DevelopmentSectionActions,
    modifier: Modifier = Modifier
)
{
    ActionCard(
        title = "Export installed apps",
        descriptionParagraphs = listOf(
            "You will be prompted to select a directory. "
                    + "The export will contain a list of apps installed on this device and their icons."
                    + "These files help mock the Bridge JS to Android API for development purposes.",
            "More information is available on the project home page - link in the \"About Bridge\" section near the bottom of this screen.",
        ),
        modifier = modifier,
    )
    {
        Btn(
            text = "Export",
            suffixIcon = R.drawable.ic_save_to_device,
            disabled = state.isExportDisabled,
            onClick = { actions.exportMockFolder() }
        )
    }
}


// PREVIEWS

@Composable
fun SettingsScreen2DevelopmentSectionPreview(
    isExportDisabled: Boolean = false
)
{
    PreviewWithSurfaceAndPadding {
        SettingsScreen2DevelopmentSectionContent(
            state = SettingsScreen2DevelopmentSectionState(
                isExportDisabled = isExportDisabled,
            ),
            actions = SettingsScreen2DevelopmentSectionActions.empty()
        )
    }
}

@Composable
@PreviewLightDark
fun SettingsScreen2DevelopmentSectionPreview01()
{
    SettingsScreen2DevelopmentSectionPreview()
}

@Composable
@PreviewLightDark
fun SettingsScreen2DevelopmentSectionPreview02()
{
    SettingsScreen2DevelopmentSectionPreview(
        isExportDisabled = true
    )
}