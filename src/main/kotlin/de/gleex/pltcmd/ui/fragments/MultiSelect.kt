package de.gleex.pltcmd.ui.fragments

import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.component.Fragment
import org.hexworks.zircon.api.extensions.onTextChanged
import org.hexworks.zircon.api.graphics.Symbols

class MultiSelect(val width: Int) : Fragment {
    override val root = Components.hbox().
            withSize(width, 1).
            build().apply {
                    val leftButton = Components.
                            button().
                            withText(Symbols.ARROW_LEFT.toString()).
                            build()

                    val rightButton = Components.
                            button().
                            withText(Symbols.ARROW_RIGHT.toString()).
                            build()

                    val labelWidth = width - (leftButton.width + rightButton.width)
                    val label = Components.label().
                            withSize(labelWidth, 1).
                            build().apply { onTextChanged {
                                    // center the text
                                    var textToCenter = text.trim()
                                    val spacesCount = (contentSize.width - text.length) / 2
                                    (1..spacesCount).forEach { textToCenter = " $textToCenter" }
                                    text = textToCenter
                                } }

                    label.textProperty.value = "value"

                    addComponent(leftButton)
                    addComponent(label)
                    addComponent(rightButton)
                }
}