package de.gleex.pltcmd.ui.fragments

import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.extensions.handleMouseEvents
import org.hexworks.zircon.api.uievent.MouseEventType
import org.hexworks.zircon.api.uievent.UIEventResponse

class MousePosition(override val width: Int, private val componentToWatch: Component) : BaseFragment {
    override val root = Components.hbox().
            withSize(width, 1).
            build().
            apply {
                addComponent(Components.
                        label().
                        withSize(width, 1).
                        withText("Mouse pos: 0 | 0").
                        build().
                        apply { componentToWatch.handleMouseEvents(MouseEventType.MOUSE_MOVED) {
                            mouseEvent, _ ->
                        val pos = mouseEvent.position - componentToWatch.absolutePosition
                        text = "Mouse pos: ${pos.x} | ${pos.y}"
                        UIEventResponse.pass()
                    }
                })
            }
}