package de.gleex.pltcmd.game.engine

import de.gleex.pltcmd.game.engine.entities.EntityFactory
import de.gleex.pltcmd.game.engine.entities.EntitySet
import de.gleex.pltcmd.game.engine.entities.newBaseAt
import de.gleex.pltcmd.game.engine.entities.toEntity
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.FOBEntity
import de.gleex.pltcmd.game.engine.entities.types.asElementEntity
import de.gleex.pltcmd.game.engine.entities.types.onDefeat
import de.gleex.pltcmd.game.engine.extensions.AnyGameEntity
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.logIdentifier
import de.gleex.pltcmd.game.ticks.Ticker
import de.gleex.pltcmd.game.ticks.subscribeToTicks
import de.gleex.pltcmd.model.elements.CommandingElement
import de.gleex.pltcmd.model.faction.Faction
import de.gleex.pltcmd.model.radio.RadioSender
import de.gleex.pltcmd.model.signals.radio.RadioPower
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateRectangle
import de.gleex.pltcmd.util.events.globalEventBus
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.hexworks.amethyst.api.Engine
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.databinding.api.extension.toProperty
import kotlin.random.Random
import kotlin.time.measureTimedValue

private val log = KotlinLogging.logger {}

data class Game(val engine: Engine<GameContext>, val world: WorldMap, val playerFaction: Faction, val random: Random) {

    private val allEntities: EntitySet<EntityType> = EntitySet()

    private var previousUpdate: Job? = null

    init {
        // TODO implement better handling of ticks and engine updates
        globalEventBus.subscribeToTicks {
            if (previousUpdate?.isActive == true) {
                log.warn { "processing of previous tick has not finished yet! Waiting before doing tick #${it.id}" }
                runBlocking {
                    previousUpdate?.join()
                }
            }
            previousUpdate = engine.start(context())
        }
    }

    /**
     * Creates a [GameContext] for the current tick.
     */
    fun context(): GameContext = GameContext(Ticker.currentTick, world, playerFaction, allEntities, random)

    /**
     * Adds the given entity to the engine and returns it to make chained calls possible.
     */
    fun <T : EntityType> addEntity(entity: GameEntity<T>) = entity.also {
        engine.addEntity(it)
        allEntities.add(it)
        runBlocking {
            entity.asElementEntity { element ->
                removeOnDefeat(element)
            }
        }
    }

    private fun removeOnDefeat(element: ElementEntity) {
        element onDefeat { removeEntity(element) }
    }

    private fun removeEntity(entity: AnyGameEntity) {
        log.debug { "Removing entity ${entity.logIdentifier} from game" }
        allEntities.remove(entity)
        engine.removeEntity(entity)
    }

    /**
     * Adds a new element in the given sector and returns it. If [playerControlled] is false the element will be wandering.
     */
    fun addElementInSector(
        sector: Sector,
        element: CommandingElement,
        positionInSector: Coordinate = sector.randomCoordinate(random),
        faction: Faction,
        playerControlled: Boolean = false
    ): ElementEntity {
        val elementPosition = positionInSector.toProperty()
        val radioSender = RadioSender(elementPosition, RadioPower(), world)
        val elementEntity = if (playerControlled) {
            element.toEntity(elementPosition, faction, radioSender, world)
        } else {
            EntityFactory.newWanderingElement(element, elementPosition, faction, radioSender, world)
        }
        log.debug { "Adding ${element.description} with callsign ${element.callSign} to engine at position $positionInSector" }
        return addEntity(elementEntity)
    }

    /**
     * Gets all [ElementEntity]s at the given coordinate.
     */
    fun elementsAt(location: Coordinate): Collection<ElementEntity> {
        val (elements, duration) = measureTimedValue {
            allEntities.elementsAt(location)
        }
        log.trace { "Finding ${elements.size} of ${allEntities.filterElements().size} elements at $location took ${duration.inWholeMilliseconds} ms" }
        return elements
    }

    /**
     * @return the first [ElementEntity] found at the given location or null, if none found.
     */
    fun firstElementAt(location: Coordinate): ElementEntity? =
        allEntities.firstElementAt(location)

    /**
     * Creates a main base in the given sector.
     */
    fun newHQIn(sector: Sector, faction: Faction): FOBEntity =
        world
            .newBaseAt(sector.bestFobLocation, faction)
            .also {
                addEntity(it)
            }

    /**
     * Finds the highest [Coordinate] in the center area of this sector.
     */
    private val Sector.bestFobLocation: Coordinate
        get() {
            // find the highest terrain in a 5*5 area in the center of the sector
            val centerArea = CoordinateRectangle(centerCoordinate.movedBy(-2, -2), 5, 5)
            return (this intersect centerArea)
                .tiles
                .maxByOrNull { it.terrain.height }
                ?.coordinate!!
        }
}
