package de.gleex.pltcmd.game.ui.fragments.tileinformation

import de.gleex.pltcmd.game.engine.Game
import de.gleex.pltcmd.game.engine.entities.types.SeeingEntity
import de.gleex.pltcmd.game.engine.entities.types.vision
import de.gleex.pltcmd.game.ui.renderers.SignalVisualizer
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.databinding.api.binding.Binding
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.cobalt.databinding.internal.binding.ComputedDualBinding
import org.hexworks.cobalt.logging.api.LoggerFactory
import org.hexworks.zircon.api.Fragments

/**
 * A fragment that uses a [SignalVisualizer] to draw signals of the currently selected element (if present).
 */
class SignalVisualizationFragment(
        currentTile: ObservableValue<Coordinate>,
        private val game: Game,
        private val visualizer: SignalVisualizer
) : InfoSidebarFragment(currentTile, "Draw signal", 1) {

    companion object {
        private val log = LoggerFactory.getLogger(SignalVisualizationFragment::class)
    }

    private val values: List<Pair<String, (SeeingEntity) -> Unit>> = listOf(
            "None" to { visualizer.deactivate() },
            "Vision" to { entity: SeeingEntity ->
                visualizer.activateWith(entity.vision)
            },
            "Radio" to { visualizer.deactivate(); log.warn("Not yet visualizing radio signals!") })

    private val selectedAction: Property<(SeeingEntity) -> Unit> = values.first().second.toProperty()

    private val select = Fragments
            .multiSelect(SUB_COMPONENT_WIDTH, values)
            .withToStringMethod { it.first }
            .withCallback { _, newPair -> selectedAction.updateValue(newPair.second) }
            .build()

    private val visualizerUpdate: Binding<Unit> = ComputedDualBinding(currentInfoTile, selectedAction) { coordinate, drawSignal ->
        game
                .firstElementAt(coordinate)
                .fold(
                        whenEmpty = { visualizer.deactivate() },
                        whenPresent = { elementToVisualize -> drawSignal(elementToVisualize) }
                )
    }

    init {
        componentsContainer
                .addFragment(select)
    }
}