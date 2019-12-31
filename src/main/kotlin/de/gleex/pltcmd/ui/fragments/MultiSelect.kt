package de.gleex.pltcmd.ui.fragments

import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.component.Fragment
import org.hexworks.zircon.api.extensions.onTextChanged
import org.hexworks.zircon.api.graphics.Symbols

class MultiSelect<T>(private val width: Int, private val values: List<T>) : Fragment {

    private var currentIndex = 0

    private val rightButton = Components.
            button().
            withText(Symbols.ARROW_RIGHT.toString()).
            build()

    private val leftButton = Components.
            button().
            withText(Symbols.ARROW_LEFT.toString()).
            build()

    private val label = Components.label().
            withSize(width - (leftButton.width + rightButton.width), 1).
            build().
            apply {
                onTextChanged {
                    // center the text
                    var textToCenter = it.newValue.trim()
                    val spacesCount = (contentSize.width - textToCenter.length) / 2
                    (1..spacesCount).forEach { textToCenter = " $textToCenter" }
                    textProperty.value = textToCenter
                }
            }

    override val root = Components.hbox().
            withSize(width, 1).
            build().
            apply {
                setValue(values[0])
                addComponent(leftButton)
                addComponent(label)
                addComponent(rightButton)
                }

    private fun setValue(newValue: T) {
        label.text = newValue.toString()
    }

    private fun nextValue(): T {
        currentIndex++
        if(currentIndex >= values.size) {
            currentIndex = 0
        }
        return values[currentIndex]
    }

    private fun prevValue(): T {
        currentIndex--
        if(currentIndex <= 0) {
            currentIndex = values.size - 1
        }
        return values[currentIndex]
    }
}