package de.gleex.pltcmd.game.ui.fragments.tileinformation

import de.gleex.pltcmd.game.engine.Game
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.game.ui.renderers.SignalVisualizer
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.property.Property
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
                visualizer.activateWith(seeingEntity.visionImmutable)
            },
            "Radio" to { communicatingEntity: CommunicatingEntity ->
                visualizer.activateWith(communicatingEntity.radioSignal)
            }
    )

    private val selectedAction: Property<(ElementEntity) -> Unit> = namedActions.values.first().toProperty()

    private val select = Fragments
            .multiSelect(SUB_COMPONENT_WIDTH, namedActions.toList())
            .withToStringMethod { it.first }
            .withCallback { _, newPair -> selectedAction.updateValue(newPair.second) }
            .build()

    init {
        // React to updates of currentInfoTile as well as new selection of the multiselect
        ComputedDualBinding(currentInfoTile, selectedAction) { coordinate, drawSignal ->
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