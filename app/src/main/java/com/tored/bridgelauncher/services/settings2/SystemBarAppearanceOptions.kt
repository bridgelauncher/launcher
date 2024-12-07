package com.tored.bridgelauncher.services.settings2

import com.tored.bridgelauncher.utils.RawRepresentable

enum class SystemBarAppearanceOptions(override val rawValue: Int) : RawRepresentable<Int>
{
    Hide(0),
    LightIcons(1),
    DarkIcons(2),
}