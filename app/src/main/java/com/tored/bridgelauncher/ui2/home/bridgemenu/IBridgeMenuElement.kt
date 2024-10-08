package com.tored.bridgelauncher.ui2.home.bridgemenu

sealed interface IBridgeMenuElement
{
    object Divider : IBridgeMenuElement
    data class Button(val iconResId: Int, val text: String, val action: () -> Unit) : IBridgeMenuElement
}