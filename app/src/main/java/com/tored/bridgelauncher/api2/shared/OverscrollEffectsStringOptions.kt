package com.tored.bridgelauncher.api2.shared

import com.tored.bridgelauncher.utils.RawRepresentable
import com.tored.bridgelauncher.utils.q
import com.tored.bridgelauncher.utils.serialization.StringEnumWriteOnlySerializer
import kotlinx.serialization.Serializable

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = StringEnumWriteOnlySerializer::class)
enum class OverscrollEffectsStringOptions(override val rawValue: String) : RawRepresentable<String>
{
    Default("default"),
    None("none"),
    ;

    companion object
    {
        fun fromDrawWebViewOverscrollEffects(it: Boolean) = when (it)
        {
            true -> Default
            false -> None
        }

        fun drawWebViewOverscrollEffectsOrThrow(effects: String): Boolean
        {
            return when (effects)
            {
                Default.rawValue -> true
                None.rawValue -> false
                else -> throw Exception("Argument \"effects\" must be either ${q(Default)} or ${q(None)} (got ${q(effects)}.")
            }
        }
    }
}