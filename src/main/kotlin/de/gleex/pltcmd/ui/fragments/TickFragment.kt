package de.gleex.pltcmd.ui.fragments

import de.gleex.pltcmd.events.ticks.Ticker
import org.hexworks.cobalt.databinding.api.binding.bindPlusWith
import org.hexworks.cobalt.databinding.api.binding.bindTransform
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.uievent.ComponentEventType

class TickFragment(override val width: Int) : BaseFragment {
    override val root = Components.
                            vbox().
                            withSize(width, 3).
                            build().
                            apply {
                                addComponent(Components.header().withText("Current Tick"))
                                addComponent(Components.
                                    label().
                                    withSize(contentSize.width, 1).
                                    build().
                                    apply {
                                        textProperty.updateFrom(
                                                Ticker.currentTickObservable.bindTransform { it.toString() }
                                                        bindPlusWith createPropertyFrom(": ")
                                                        bindPlusWith Ticker.currentTimeString)
                                    })
                                addComponent(Components.
                                    button().
                                    withText("TICK!").
                                    build().
                                    apply {
                                        processComponentEvents(ComponentEventType.ACTIVATED) { Ticker.tick() }
                                    })
                            }
}