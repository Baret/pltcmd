package de.gleex.pltcmd.game.ui.strings

import org.hexworks.cobalt.databinding.api.value.ObservableValue

/**
 * This class makes extending [FrontendString] as simple as possible.
 *
 * It gets a map of [FrontendString.Length] to toString-transformation. For every length that is not present
 * in that map it falls back to the default behavior of [DefaultFrontendString]
 */
abstract class SimpleFrontendString<T: Any>(
        originalObject: ObservableValue<T>,
        length: FrontendString.Length = FrontendString.Length.FULL
) : DefaultFrontendString<T>(originalObject, length) {
    abstract val mapping: Map<FrontendString.Length, (T) -> String>

    override fun transform(value: T, length: FrontendString.Length): String {
        return mapping.getOrDefault(length) {
            super.transform(value, length)
        }.invoke(value)
    }
}