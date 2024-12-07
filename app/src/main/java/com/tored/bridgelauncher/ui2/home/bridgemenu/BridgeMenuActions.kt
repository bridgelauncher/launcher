package com.tored.bridgelauncher.ui2.home.bridgemenu

data class BridgeMenuActions(
    val onWebViewRefreshRequest: () -> Unit,
    val onHideBridgeButtonRequest: () -> Unit,
    val onRequestIsExpandedChange: (newIsExpanded: Boolean) -> Unit,
)
{
    companion object
    {
        fun empty() = BridgeMenuActions({}, {}, {})
    }
}