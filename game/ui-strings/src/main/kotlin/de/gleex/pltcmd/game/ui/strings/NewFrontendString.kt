package de.gleex.pltcmd.game.ui.strings

import org.hexworks.cobalt.databinding.api.binding.bindPlusWith
import org.hexworks.cobalt.databinding.api.binding.bindTransform
import org.hexworks.cobalt.databinding.api.event.ObservableValueChanged
import org.hexworks.cobalt.databinding.api.value.ObservableValue

class NewFrontendString<T : Any>(
        originalObject: ObservableValue<T>,
        override val format: Format,
        transformation: (T, Format) -> String
): FrontendString<T> {

    private val internalBinding = originalObject.bindTransform { transformation(it, format) }

    override val value: String
        get() = internalBinding.value

    override fun onChange(fn: (ObservableValueChanged<String>) -> Unit) =
            internalBinding.onChange(fn)

    override fun plus(other: FrontendString<Any>): FrontendString<*> =
            DefaultFrontendString(internalBinding.bindPlusWith(other), de.gleex.pltcmd.game.ui.strings.extensions.minOf(format, other.format))
}
