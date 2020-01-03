package de.gleex.pltcmd.ui.fragments

import org.hexworks.cobalt.databinding.api.createPropertyFrom
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.component.Fragment
import org.hexworks.zircon.api.extensions.processComponentEvents
import org.hexworks.zircon.api.graphics.Symbols
import org.hexworks.zircon.api.uievent.ComponentEventType

class MultiSelect<T>(private val width: Int, private val values: List<T>, private val callback: (T) -> Unit) : Fragment {

    private val indexProperty = createPropertyFrom(0)

    private val rightButton = Components.
            button().
            withText(Symbols.ARROW_RIGHT.toString()).
            build().
            apply { processComponentEvents(ComponentEventType.ACTIVATED) { nextValue()} }

    private val leftButton = Components.
            button().
            withText(Symbols.ARROW_LEFT.toString()).
            build().
            apply { processComponentEvents(ComponentEventType.ACTIVATED) { prevValue()} }

    private val label = Components.label().
            withSize(width - (leftButton.width + rightButton.width), 1).
            build().
            apply {
                text = getStringValue(0)
                textProperty.updateFrom(indexProperty) { i -> getStringValue(i) }
            }

    private fun getStringValue(index: Int) = values[index].toString()

    override val root = Components.hbox().
            withSize(width, 1).
            build().
            apply {
                addComponent(leftButton)
                addComponent(label)
                addComponent(rightButton)
                }

    private fun setValue(index: Int) {
        indexProperty.value = index
        callback.invoke(values[indexProperty.value])
    }

    private fun nextValue() {
        var nextIndex = indexProperty.value + 1
        if(nextIndex >= values.size) {
            nextIndex = 0
        }
        setValue(nextIndex)
    }

    private fun prevValue() {
        var prevIndex = indexProperty.value - 1
        if(prevIndex < 0) {
            prevIndex = values.size - 1
        }
        setValue(prevIndex)
    }
}