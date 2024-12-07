package com.tored.bridgelauncher.api2.shared

import com.tored.bridgelauncher.utils.RawRepresentable
import com.tored.bridgelauncher.utils.q
import com.tored.bridgelauncher.utils.serialization.StringEnumWriteOnlySerializer
import kotlinx.serialization.Serializable

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = StringEnumWriteOnlySerializer::class)
enum class BridgeButtonVisibilityStringOptions(override val rawValue: String) : RawRepresentable<String>
{
    Shown("shown"),
    Hidden("hidden"),
    ;

    companion object
    {
        fun fromShowBridgeButton(it: Boolean) = when (it)
        {
            true -> Shown
            false -> Hidden
        }

        fun showBridgeButtonFromStringOrThrow(visibility: String): Boolean
        {
            return when (visibility)
            {
                Shown.rawValue -> true
                Hidden.rawValue -> false
                else -> throw Exception("Argument \"visibility\" must be either ${q(Shown)} or ${q(Hidden)} (got ${q(visibility)}).")
            }
        }
    }
}