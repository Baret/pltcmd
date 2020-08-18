package de.gleex.pltcmd.game.ui.components

import org.hexworks.cobalt.logging.api.LoggerFactory
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.component.Fragment
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.data.Position

/**
 * Workaround to wrap [Fragment]s into a [Panel].
 *
 * This need docs TODO
 */
object CustomComponent {
    private val log = LoggerFactory.getLogger(CustomComponent::class)

    operator fun invoke(fragmentToWrap: Fragment, position: Position): Panel {
        val size = fragmentToWrap.root.size
        log.debug("Creating custom component with size $size at $position for fragment $fragmentToWrap")
        return Components.panel()
                .withSize(size)
                .withPosition(position)
                .build()
                .apply {
                    addFragment(fragmentToWrap)
                }
    }
}