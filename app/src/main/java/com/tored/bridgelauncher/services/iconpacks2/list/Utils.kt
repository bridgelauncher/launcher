package com.tored.bridgelauncher.services.iconpacks2.list

import kotlinx.coroutines.flow.first

suspend fun InstalledIconPacksHolder.getPackageNameToIconPackMap(): Map<String, IconPackInfo>
{
    return packageNameToIconPackMap.first { it != null }!!
}

suspend fun InstalledIconPacksHolder.getIconPack(packageName: String): IconPackInfo?
{
    return getPackageNameToIconPackMap()[packageName]
}

