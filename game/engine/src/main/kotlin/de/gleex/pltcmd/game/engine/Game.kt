package de.gleex.pltcmd.game.engine

import de.gleex.pltcmd.game.engine.entities.EntityFactory
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.options.GameOptions
import de.gleex.pltcmd.game.ticks.TickId
import de.gleex.pltcmd.game.ticks.subscribeToTicks
import de.gleex.pltcmd.model.elements.*
import de.gleex.pltcmd.model.radio.RadioSender
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.util.events.globalEventBus
import org.hexworks.amethyst.api.Engine
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.random.Random

data class Game(val engine: Engine<GameContext>, val world: WorldMap, val random: Random) {

    init {
        globalEventBus.subscribeToTicks {
            val currentTick = it.id
            engine.update(context(currentTick))
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(Game::class)
    }

    /**
     * Creates a [GameContext] for the given tick.
     */
    fun context(tick: TickId): GameContext = GameContext(tick, world, random)

    /**
     * Adds the given entity to the engine and returns it to make chained calls possible.
     */
    fun <T : EntityType> addEntity(entity: GameEntity<T>) = entity.also { engine.addEntity(it) }

    /**
     * Adds a new element in the given sector and returns it.
     */
    fun addElementInSector(sector: Sector, callsign: String = "Element ${random.nextInt(999_999)}", affiliation: Affiliation = Affiliation.Unknown): ElementEntity {
        val positionInSector = sector.randomCoordinate(random)
        val callSign = CallSign(callsign)
        val element = Element(callSign, setOf(GenericUnit(UnitType.Soldier)))
        // FIXME radio location is fix but should observe the position of the entity
        val radioSender = RadioSender(positionInSector, GameOptions.defaultRadioPower, world)
        val elementEntity = if (affiliation == Affiliation.Friendly || affiliation == Affiliation.Self) {
            EntityFactory.newElement(element, positionInSector, affiliation, radioSender)
        } else {
            EntityFactory.newWanderingElement(element, positionInSector, affiliation, radioSender)
        }
        log.debug("Adding element with callsign $callSign to engine at position $positionInSector")
        return addEntity(elementEntity)
    }

}