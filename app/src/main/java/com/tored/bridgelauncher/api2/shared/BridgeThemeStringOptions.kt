package com.tored.bridgelauncher.api2.shared

import com.tored.bridgelauncher.services.settings2.BridgeThemeOptions
import com.tored.bridgelauncher.utils.RawRepresentable
import com.tored.bridgelauncher.utils.q
import com.tored.bridgelauncher.utils.serialization.StringEnumWriteOnlySerializer
import kotlinx.serialization.Serializable

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = StringEnumWriteOnlySerializer::class)
enum class BridgeThemeStringOptions(override val rawValue: String) : RawRepresentable<String>
{
    System("system"),
    Light("light"),
    Dark("dark"),
    ;

    companion object
    {
        fun fromBridgeTheme(theme: BridgeThemeOptions) = when (theme)
        {
            BridgeThemeOptions.System -> System
            BridgeThemeOptions.Light -> Light
            BridgeThemeOptions.Dark -> Dark
        }

        fun bridgeThemeFromStringOrThrow(theme: String): BridgeThemeOptions
        {
            return when (theme)
            {
                System.rawValue -> BridgeThemeOptions.System
                Light.rawValue -> BridgeThemeOptions.Light
                Dark.rawValue -> BridgeThemeOptions.Dark
                else -> throw Exception("Argument \"theme\" must be one of ${q(System)}, ${q(Light)} or ${q(Dark)} (got ${q(theme)}).")
            }
        }
    }
}