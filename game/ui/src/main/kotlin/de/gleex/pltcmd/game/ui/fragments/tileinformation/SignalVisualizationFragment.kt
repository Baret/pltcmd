package de.gleex.pltcmd.game.ui.fragments.tileinformation

import de.gleex.pltcmd.game.engine.Game
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.game.ui.renderers.SignalVisualizer
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.util.debug.DebugFeature
import org.hexworks.cobalt.databinding.api.extension.fold
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.zircon.api.dsl.fragment.buildSelector

/**
 * A fragment that uses a [SignalVisualizer] to draw signals of the currently selected element (if present).
 */
@DebugFeature
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

    private val select = buildSelector<Pair<String, (ElementEntity) -> Unit>> {
        width = SUB_COMPONENT_WIDTH
        valueList = namedActions.toList()
        toStringMethod = { it.first }
    }

    init {
        // React to updates of currentInfoTile as well as new selection of the multiselect
        currentInfoTile.onChange { updateVisualization(it.newValue, select.selected.second) }
        select.selectedValue.onChange { updateVisualization(currentInfoTile.value, it.newValue.second) }

        componentsContainer.addFragment(select)
    }

    private fun updateVisualization(
        coordinate: Coordinate,
        drawSignal: (ElementEntity) -> Unit
    ) {
        game
            .firstElementAt(coordinate)
            .fold(
                whenNull = { visualizer.deactivate() },
                whenNotNull = { elementToVisualize -> drawSignal(elementToVisualize) }
            )
    }
}