package de.gleex.pltcmd.game.ui.fragments.tileinformation

import de.gleex.pltcmd.game.options.UiOptions
import de.gleex.pltcmd.game.ui.fragments.tileinformation.InfoSidebarFragment.Companion.SUB_COMPONENT_WIDTH
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.Container

/**
 * A fragment used to be put into the info sidebar. It has a fixed form of:
 *
 * ```
 * title:
 *   component1
 *   component2
 *   ...
 * ```
 * This ensures a consistent look and feel of the sidebar.
 *
 * The title is being put into a header. The components should not be headers and have a maximum
 * width of [SUB_COMPONENT_WIDTH] as they are indented. Simply add them to [componentsContainer].
 */
abstract class InfoSidebarFragment(
        currentTile: ObservableValue<Coordinate>,
        /**
         * The title put into a header.
         */
        title: String,
        /**
         * The height needed of all components after the header.
         */
        neededHeight: Int
) : TileInformationFragment(currentTile) {

    companion object {
        private val FRAGMENT_WIDTH = UiOptions.SIDEBAR_WIDTH - 2
        private val INDENTATION = 2
        val SUB_COMPONENT_WIDTH = FRAGMENT_WIDTH - INDENTATION
    }

    final override val fragmentWidth: Int = FRAGMENT_WIDTH

    /**
     * The container to pick up all the components needed in this fragment.
     */
    protected val componentsContainer: Container = Components
            .vbox()
            .withSpacing(0)
            .withSize(SUB_COMPONENT_WIDTH, neededHeight)
            .build()
            .apply {
                moveRightBy(INDENTATION)
            }

    final override val root: Component = Components
            .vbox()
            .withSpacing(0)
            .withSize(fragmentWidth, 1 + neededHeight)
            .build()
            .apply {
                addComponent(Components
                        .header()
                        .withText(title)
                        .build())
                addComponent(componentsContainer)
            }
}