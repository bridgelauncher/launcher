package com.tored.bridgelauncher.ui2.home.bridgemenu

import androidx.compose.runtime.Immutable

@Immutable
data class BridgeMenuState(
    val isShown: Boolean,
    val isExpanded: Boolean,
    val showAppDrawerButtonWhenCollapsed: Boolean
)