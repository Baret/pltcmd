package de.gleex.pltcmd.ui.fragments

import org.hexworks.cobalt.databinding.api.createPropertyFrom
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.component.Fragment
import org.hexworks.zircon.api.graphics.Symbols
import org.hexworks.zircon.api.uievent.ComponentEventType
import kotlin.math.min

/**
 * A MultiSelect is a one line input to select one of multiple values of type [T].
 * It gets a list of objects you can cycle through with a left and a right button.
 * You must provide the width of the [Fragment], the list of values and a callBack method.
 * This callback will be invoked when the value changes (i.e. the uer pressed one of the buttons) and gets the new Value as parameter.
 * Optionally you can specify if the text on the label should be centered, default is true.
 * If the toString method of [T] is not well suited for the label, you can pass a different one.
 */
class MultiSelect<T : Any>(
        override val width: Int,
        private val values: List<T>,
        private val callback: (T) -> Unit,
        private val centeredText: Boolean = true,
        private val toStringMethod: (T) -> String = Any::toString
) : BaseFragment {

    private val indexProperty = createPropertyFrom(0)

    private val rightButton = Components.button().
            withText(Symbols.ARROW_RIGHT.toString()).
            withDecorations().
            build().
            apply { processComponentEvents(ComponentEventType.ACTIVATED) { nextValue() } }

    private val leftButton = Components.button().
            withText(Symbols.ARROW_LEFT.toString()).
            withDecorations().
            build().
            apply { processComponentEvents(ComponentEventType.ACTIVATED) { prevValue() } }

    private val label = Components.label().
            withSize(width - (leftButton.width + rightButton.width), 1).
            build()

    override val root = Components.hbox().
            withSize(width, 1).
            withSpacing(0).
            build().
            apply {
                addComponent(leftButton)
                addComponent(label)
                addComponent(rightButton)

                label.apply {
                    text = getStringValue(0)
                    textProperty.updateFrom(indexProperty) { i -> getStringValue(i) }
                }
            }

    private fun setValue(index: Int) {
        indexProperty.value = index
        callback.invoke(values[indexProperty.value])
    }

    private fun nextValue() {
        var nextIndex = indexProperty.value + 1
        if (nextIndex >= values.size) {
            nextIndex = 0
        }
        setValue(nextIndex)
    }

    private fun prevValue() {
        var prevIndex = indexProperty.value - 1
        if (prevIndex < 0) {
            prevIndex = values.size - 1
        }
        setValue(prevIndex)
    }

    private fun getStringValue(index: Int) = toStringMethod.invoke(values[index]).centered()

    private fun String.centered(): String {
        val maxWidth = label.contentSize.width
        return if (centeredText && length < maxWidth) {
            val spacesCount = (maxWidth - length) / 2
            this.padStart(spacesCount + length)
        } else {
            this.substring(0, min(length, maxWidth))
        }
    }
}
