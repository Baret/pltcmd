package de.gleex.pltcmd.game.engine

import de.gleex.pltcmd.game.engine.entities.EntityFactory
import de.gleex.pltcmd.game.engine.entities.toEntityAt
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.ElementType
import de.gleex.pltcmd.game.engine.entities.types.callsign
import de.gleex.pltcmd.game.engine.entities.types.combatStats
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.options.GameOptions
import de.gleex.pltcmd.game.ticks.Ticker
import de.gleex.pltcmd.game.ticks.subscribeToTicks
import de.gleex.pltcmd.model.elements.Affiliation
import de.gleex.pltcmd.model.elements.CommandingElement
import de.gleex.pltcmd.model.radio.RadioSender
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.util.events.globalEventBus
import org.hexworks.amethyst.api.Engine
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.random.Random

data class Game(val engine: Engine<GameContext>, val world: WorldMap, val random: Random) {

    private val allElements: MutableSet<ElementEntity> = mutableSetOf()

    companion object {
        private val log = LoggerFactory.getLogger(Game::class)
    }

    init {
        globalEventBus.subscribeToTicks {
            engine.update(context())
        }
    }

    /**
     * Creates a [GameContext] for the current tick.
     */
    fun context(): GameContext = GameContext(Ticker.currentTick, world, allElements.toSet(), random)

    /**
     * Adds the given entity to the engine and returns it to make chained calls possible.
     */
    fun <T : EntityType> addEntity(entity: GameEntity<T>) = entity.also {
        engine.addEntity(it)
        if (it.type is ElementType) {
            val element = it as ElementEntity
            allElements.add(element)
            removeOnDeath(element)
        }
    }

    private fun removeOnDeath(element: ElementEntity) {
        element.combatStats onDeath { removeElement(element) }
    }

    private fun removeElement(element: ElementEntity) {
        log.debug("Removing element ${element.callsign} from game")
        allElements.remove(element)
        engine.removeEntity(element)
    }

    /**
     * Adds a new element in the given sector and returns it.
     */
    fun addElementInSector(
            sector: Sector,
            element: CommandingElement,
            positionInSector: Coordinate = sector.randomCoordinate(random),
            affiliation: Affiliation = Affiliation.Unknown
    ): ElementEntity {
        val elementPosition = positionInSector.toProperty()
        val radioSender = RadioSender(elementPosition, GameOptions.defaultRadioPower, world)
        val elementEntity = if (affiliation == Affiliation.Friendly || affiliation == Affiliation.Self) {
            //EntityFactory.newElement(element, elementPosition, affiliation, radioSender)
            element.toEntityAt(elementPosition, affiliation, radioSender)
        } else {
            EntityFactory.newWanderingElement(element, elementPosition, affiliation, radioSender)
        }
        log.debug("Adding ${element.description} with callsign ${element.callSign} to engine at position $positionInSector")
        return addEntity(elementEntity)
    }

}
