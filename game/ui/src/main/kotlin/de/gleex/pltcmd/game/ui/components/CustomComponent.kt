package de.gleex.pltcmd.game.ui.components

import mu.KotlinLogging
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.component.Fragment
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.data.Position

private val log = KotlinLogging.logger {}

/**
 * Workaround to wrap [Fragment]s into a [Panel]. As we can not directly extend [Components] (we would need
 * to implement the internal interface), we simulate custom components. We actually build [Fragment]s but to
 * be able to use Container.addComponent(ourCustomComponent), we wrap them in a panel by
 * calling `CustomComponent(ourFragment)`.
 */
object CustomComponent {

    /**
     * Wraps the given [Fragment] into a [Panel] with the same size and position. This kind of turns [fragmentToWrap]
     * into a [org.hexworks.zircon.api.component.Component].
     */
    operator fun invoke(fragmentToWrap: Fragment, position: Position): Panel {
        val size = fragmentToWrap.root.size
        log.debug { "Creating custom component with size $size at $position for fragment $fragmentToWrap" }
        return Components.panel()
                .withPreferredSize(size)
                .withPosition(position)
                .build()
                .apply {
                    addFragment(fragmentToWrap)
                }
    }
}