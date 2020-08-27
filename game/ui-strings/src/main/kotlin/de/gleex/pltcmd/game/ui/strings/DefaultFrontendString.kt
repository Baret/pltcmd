package de.gleex.pltcmd.game.ui.strings

import de.gleex.pltcmd.game.ui.strings.extensions.minOf
import org.hexworks.cobalt.databinding.api.binding.bindPlusWith
import org.hexworks.cobalt.databinding.api.binding.bindTransform
import org.hexworks.cobalt.databinding.api.event.ObservableValueChanged
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.cobalt.events.api.Subscription

/**
 * Default implementation of a [FrontendString] that simply uses toString(), trims the original and appends "..."
 * if it is too long. For very short lengths the string is simply cut off.
 */
open class DefaultFrontendString<T : Any>(
        private val originalObject: ObservableValue<T>,
        override val objectFormatter: FrontendString.ObjectFormatter = FrontendString.ObjectFormatter.FULL
) : FrontendString<T> {

    constructor(originalObject: T, objectFormatter: FrontendString.ObjectFormatter = FrontendString.ObjectFormatter.FULL) : this(originalObject.toProperty(), objectFormatter)

    private val internalBinding = originalObject.bindTransform { objectFormatter(it) }

    /**
     * The underlying object formatted as string with [objectFormatter].
     */
    override val value: String
        get() {
            val transformedValue = internalBinding.value
            require(transformedValue.length <= objectFormatter.length) {
                "Invalid transformation to frontend string. Required maximum length ${objectFormatter.length} but transformation resulted in length ${transformedValue.length}. Transformed object: ${originalObject.value}"
            }
            return transformedValue
        }

    /**
     * Transform the underlying object to a string of given length.
     */
    protected open operator fun FrontendString.ObjectFormatter.invoke(objectToTransform: T): String {
        val string = objectToTransform.toString()
        return if (string.length <= objectFormatter.length) {
            string
        } else {
            if (objectFormatter.length >= 6) {
                string.substring(0, objectFormatter.length - 3)
                        .padEnd(objectFormatter.length, '.')
            } else {
                string.substring(0, objectFormatter.length)
            }
        }
    }

    override fun onChange(fn: (ObservableValueChanged<String>) -> Unit): Subscription =
            internalBinding.onChange(fn)

    override fun plus(other: FrontendString<Any>): FrontendString<String> {
        return DefaultFrontendString(internalBinding.bindPlusWith(other), minOf(objectFormatter, other.objectFormatter))
    }
}
