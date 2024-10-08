package com.tored.bridgelauncher.ui2.home.bridgemenu

data class BridgeMenuActions(
    val onWebViewRefreshRequest: () -> Unit,
    val onOpenDevConsoleRequest: () -> Unit,
    val onSwitchLaunchersRequest: () -> Unit,
    val onOpenSettingsRequest: () -> Unit,
    val onHideBridgeButtonRequest: () -> Unit,
    val onOpenAppDrawerRequest: () -> Unit,
    val onRequestIsExpandedChange: (newIsExpanded: Boolean) -> Unit,
)
{
    companion object
    {
        fun Empty() = BridgeMenuActions({}, {}, {}, {}, {}, {}, {})
    }
}