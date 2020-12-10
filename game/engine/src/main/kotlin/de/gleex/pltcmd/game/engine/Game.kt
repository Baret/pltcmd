package de.gleex.pltcmd.game.engine

import de.gleex.pltcmd.game.engine.entities.EntityFactory
import de.gleex.pltcmd.game.engine.entities.EntitySet
import de.gleex.pltcmd.game.engine.entities.toEntity
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.ElementType
import de.gleex.pltcmd.game.engine.entities.types.callsign
import de.gleex.pltcmd.game.engine.entities.types.onDefeat
import de.gleex.pltcmd.game.engine.extensions.AnyGameEntity
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.ticks.Ticker
import de.gleex.pltcmd.game.ticks.subscribeToTicks
import de.gleex.pltcmd.model.elements.Affiliation
import de.gleex.pltcmd.model.elements.CommandingElement
import de.gleex.pltcmd.model.radio.RadioSender
import de.gleex.pltcmd.model.signals.radio.RadioPower
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.util.events.globalEventBus
import org.hexworks.amethyst.api.Engine
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.random.Random
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

data class Game(val engine: Engine<GameContext>, val world: WorldMap, val random: Random) {

    private val allEntities: EntitySet<EntityType> = EntitySet()

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
    fun context(): GameContext = GameContext(Ticker.currentTick, world, allEntities, random)

    /**
     * Adds the given entity to the engine and returns it to make chained calls possible.
     */
    fun <T : EntityType> addEntity(entity: GameEntity<T>) = entity.also {
        engine.addEntity(it)
        allEntities.add(it)
        if (it.type is ElementType) {
            @Suppress("UNCHECKED_CAST")
            removeOnDefeat(it as ElementEntity)
        }
    }

    private fun removeOnDefeat(element: ElementEntity) {
        element onDefeat { removeEntity(element) }
    }

    @Suppress("UNCHECKED_CAST")
    private fun removeEntity(entity: AnyGameEntity) {
        val entityName: String = if(entity.type == ElementType) {
            (entity as ElementEntity).callsign.toString()
        } else {
            entity.toString()
        }
        log.debug("Removing entity $entityName from game")
        allEntities.remove(entity)
        engine.removeEntity(entity)
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
        val radioSender = RadioSender(elementPosition, RadioPower(2000.0), world)
        val elementEntity = if (affiliation == Affiliation.Friendly || affiliation == Affiliation.Self) {
            element.toEntity(elementPosition, affiliation, radioSender)
        } else {
            EntityFactory.newWanderingElement(element, elementPosition, affiliation, radioSender)
        }
        log.debug("Adding ${element.description} with callsign ${element.callSign} to engine at position $positionInSector")
        return addEntity(elementEntity)
    }

    /**
     * Gets all [ElementEntity]s at the given coordinate.
     */
    @OptIn(ExperimentalTime::class)
    fun elementsAt(location: Coordinate): Collection<ElementEntity> {
        val (elements, duration) = measureTimedValue {
            allEntities.elementsAt(location)
        }
        if (log.isTraceEnabled()) {
            log.trace("Finding ${elements.size} of ${allEntities.allElements().size} elements at $location took ${duration.inMilliseconds} ms")
        }
        return elements
    }

    /**
     * @return a [Maybe] containing the first [ElementEntity] found at the given location.
     */
    fun firstElementAt(location: Coordinate): Maybe<GameEntity<ElementType>> =
            allEntities.firstElementAt(location)

}
