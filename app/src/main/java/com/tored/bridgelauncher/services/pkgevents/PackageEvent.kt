package com.tored.bridgelauncher.services.pkgevents

sealed class PackageEvent(val packageName: String)
{
    class Added(packageName: String) : PackageEvent(packageName)
    class Replaced(packageName: String) : PackageEvent(packageName)
    class Removed(packageName: String) : PackageEvent(packageName)
}