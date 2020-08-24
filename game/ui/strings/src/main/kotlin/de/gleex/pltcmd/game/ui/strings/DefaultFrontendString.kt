package de.gleex.pltcmd.game.ui.strings

import org.hexworks.cobalt.databinding.api.binding.bindPlusWith
import org.hexworks.cobalt.databinding.api.binding.bindTransform
import org.hexworks.cobalt.databinding.api.event.ObservableValueChanged
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.cobalt.events.api.Subscription

/**
 * Default implementation of a [FrontendString] that simply uses toString(), trims the original and appends "..."
 * if it is too long. For very short [FrontendString.Length]s the string is simply cut off.
 */
open class DefaultFrontendString<T : Any>(
        private val originalObject: ObservableValue<T>,
        override val length: FrontendString.Length = FrontendString.Length.FULL
) : FrontendString<T> {

    constructor(originalObject: T, length: FrontendString.Length = FrontendString.Length.FULL) : this(originalObject.toProperty(), length)

    private val internalBinding = originalObject.bindTransform { transform(it, length) }

    override val value: String
        get() {
            val transformedValue = internalBinding.value
            require(transformedValue.length <= length.characters) {
                "Invalid transformation to frontend string. Required maximum length ${length.characters} but transformation resulted in length ${transformedValue.length}. Transformed object: ${originalObject.value}"
            }
            return transformedValue
        }

    /**
     * Transform the underlying object to a string of given length.
     */
    protected open fun transform(value: T, length: FrontendString.Length): String {
        val string = value.toString()
        return if (string.length <= length.characters) {
            string
        } else {
            if (length.characters >= 6) {
                string.substring(0, length.characters - 3)
                        .padEnd(length.characters, '.')
            } else {
                string.substring(0, length.characters)
            }
        }
    }

    override fun onChange(fn: (ObservableValueChanged<String>) -> Unit): Subscription =
            internalBinding.onChange(fn)

    override fun plus(other: FrontendString<Any>): FrontendString<String> {
        return DefaultFrontendString(internalBinding.bindPlusWith(other), de.gleex.pltcmd.game.ui.strings.extensions.minOf(length, other.length))
    }
}