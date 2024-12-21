package com.tored.bridgelauncher.services.iconpacks2.list.events

import com.tored.bridgelauncher.services.iconpacks2.list.IconPackInfo

sealed interface IIconPackListChangeEvent
{
    data class Added(val iconPack: IconPackInfo) : IIconPackListChangeEvent
    data class Changed(val oldIconPack: IconPackInfo, val newIconPack: IconPackInfo) : IIconPackListChangeEvent
    data class Removed(val oldIconPack: IconPackInfo) : IIconPackListChangeEvent
}