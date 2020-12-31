package de.gleex.pltcmd.game.ui.fragments.tileinformation

import de.gleex.pltcmd.game.engine.Game
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.game.ui.renderers.SignalVisualizer
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.cobalt.databinding.internal.binding.ComputedDualBinding
import org.hexworks.zircon.api.Fragments

/**
 * A fragment that uses a [SignalVisualizer] to draw signals of the currently selected element (if present).
 */
class SignalVisualizationFragment(
    currentTile: ObservableValue<Coordinate>,
    private val game: Game,
    private val visualizer: SignalVisualizer
) : InfoSidebarFragment(currentTile, "Draw signal", 1) {

    /**
     * The values used in the multiselect fragment. It maps the display string to the action that will be
     * executed when the respective action has been selected.
     *
     * In other words the key will simply be used in the frontend. The value is a piece of code calling
     * the [visualizer].
     */
    private val namedActions: Map<String, (ElementEntity) -> Unit> = mapOf(
        "None" to { visualizer.deactivate() },
        "Vision" to { seeingEntity: SeeingEntity ->
            visualizer.activateWith(seeingEntity.vision)
        },
        "Radio" to { communicatingEntity: CommunicatingEntity ->
            visualizer.activateWith(communicatingEntity.radioSignal)
        }
    )

    private val select = Fragments
        .selector(SUB_COMPONENT_WIDTH, namedActions.toList())
        .withToStringMethod { it.first }
        .build()

    init {
        // React to updates of currentInfoTile as well as new selection of the multiselect
        ComputedDualBinding(currentInfoTile, select.selectedValue) { coordinate, (_, drawSignal) ->
            game
                .firstElementAt(coordinate)
                .fold(
                    whenEmpty = { visualizer.deactivate() },
                    whenPresent = { elementToVisualize -> drawSignal(elementToVisualize) }
                )
        }

        componentsContainer
            .addFragment(select)
    }
}