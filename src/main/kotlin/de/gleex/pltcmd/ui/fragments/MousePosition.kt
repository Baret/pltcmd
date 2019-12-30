package de.gleex.pltcmd.ui.fragments

import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.UIEventResponses
import org.hexworks.zircon.api.component.Fragment
import org.hexworks.zircon.api.extensions.handleMouseEvents
import org.hexworks.zircon.api.screen.Screen
import org.hexworks.zircon.api.uievent.MouseEventType

class MousePosition(val width: Int, val componentToWach: Screen) : Fragment {
    override val root = Components.
            hbox().
            withSize(width, 1).
            build().
            apply {
                addComponent(Components.
                        label().
                        withText("Mouse pos: ${Positions.create(0,0)}").
                        build().
                        apply { componentToWach.handleMouseEvents(MouseEventType.MOUSE_MOVED) {
                            mouseEvent, _ ->
                                text = "Mouse pos: ${mouseEvent.position.x} | ${mouseEvent.position.y}"
                                UIEventResponses.pass()
                        } })
            }
}