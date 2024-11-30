package com.tored.bridgelauncher.services.settings2

import com.tored.bridgelauncher.utils.RawRepresentable

enum class BridgeThemeOptions(override val rawValue: Int) : RawRepresentable<Int>
{
    System(0),
    Light(1),
    Dark(2),
}