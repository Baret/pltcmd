package de.gleex.pltcmd.game.ui.strings

import de.gleex.pltcmd.game.ui.strings.extensions.toFrontendString
import de.gleex.pltcmd.game.ui.strings.extensions.withFrontendString
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.databinding.api.binding.bindPlusWith
import org.hexworks.cobalt.databinding.api.binding.bindTransform
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.zircon.api.Components

/**
 * A frontend string is a human readable string representation of any object (i.e. model objects)
 * that is observable and thus can be bound to UI elements.
 *
 * @sample example
 */
class FrontendString<T : Any>(
        private val originalObject: ObservableValue<T>,
        val format: Format,
        transform: Transformation<T>
) : ObservableValue<String> by originalObject.bindTransform({
    it.transform(format).also { transformed ->
            require(transformed.length <= format.length) {
                "Invalid transformation to frontend string. Required maximum length ${format.length} but transformation resulted in length ${transformed.length}. Transformed object: ${originalObject.value}"
            }
        }
}) {

    /**
     * Creates a new [FrontendString] with both strings combined. This acts like a bindPlusWith call. The resulting
     * frontend string will have the shorter of both lengths. This means the resulting string will not be longer
     * than any one of the two.
     */
    operator fun plus(other: FrontendString<*>): FrontendString<*> {
        return bindPlusWith(other)
                .toFrontendString(minOf(format, other.format))
    }

    override fun toString(): String {
        return "FrontendString(originalObject=$originalObject, format=$format, value=$value)"
    }
}

private fun example() {
    Components.label()
            // ...
            .build()
            .withFrontendString(
                    Coordinate(100, 100)
                            .toFrontendString(Format.SHORT5))
}