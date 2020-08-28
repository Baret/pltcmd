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
        override val format: FrontendString.Format = FrontendString.Format.FULL
) : FrontendString<T> {

    constructor(originalObject: T, format: FrontendString.Format = FrontendString.Format.FULL) : this(originalObject.toProperty(), format)

    private val internalBinding = originalObject.bindTransform { format(it) }

    /**
     * The underlying object formatted as string with [FrontendString.format].
     */
    override val value: String
        get() {
            val transformedValue = internalBinding.value
            require(transformedValue.length <= format.length) {
                "Invalid transformation to frontend string. Required maximum length ${format.length} but transformation resulted in length ${transformedValue.length}. Transformed object: ${originalObject.value}"
            }
            return transformedValue
        }

    /**
     * Transform the underlying object to a string of given length.
     */
    protected open operator fun FrontendString.Format.invoke(objectToTransform: T): String {
        val string = objectToTransform.toString()
        return if (string.length <= format.length) {
            string
        } else {
            if (format.length >= 6) {
                string.substring(0, format.length - 3)
                        .padEnd(format.length, '.')
            } else {
                string.substring(0, format.length)
            }
        } 
    }

    override fun onChange(fn: (ObservableValueChanged<String>) -> Unit): Subscription =
            internalBinding.onChange(fn)

    override fun plus(other: FrontendString<Any>): FrontendString<String> {
        return DefaultFrontendString(internalBinding.bindPlusWith(other), minOf(format, other.format))
    }
}
