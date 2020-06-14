package de.gleex.pltcmd.game.engine

import de.gleex.pltcmd.game.engine.entities.EntityFactory
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.model.elements.Affiliation
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.elements.Elements
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap
import org.hexworks.amethyst.api.Engine
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.random.Random

data class Game(val engine: Engine<GameContext>, val world: WorldMap, val random: Random) {

    companion object {
        private val log = LoggerFactory.getLogger(Game::class)
    }

    /**
     * Creates a [GameContext] for the given tick.
     */
    fun context(tick: Int): GameContext = GameContext(tick, world, random)

    /**
     * Adds the given entity to the engine and returns it to make chained calls possible.
     */
    fun <T : EntityType> addEntity(entity: GameEntity<T>) = entity.also { engine.addEntity(it) }

    /**
     * Adds a new element in the given sector and returns it. If it was not possible to add the returned value is null.
     */
    fun addElementInSector(sector: Sector, callsign: String = "Element ${random.nextInt(999_999)}", affiliation: Affiliation = Affiliation.Unknown): ElementEntity {
        val positionInSector = sector.randomCoordinate(random)
        val callSign = CallSign(callsign)
        val element = Elements.riflePlatoon.new()
        val elementEntity = if(affiliation == Affiliation.Friendly) {
                EntityFactory.newElement(element, positionInSector, affiliation)
            } else {
                EntityFactory.newWanderingElement(element, positionInSector, affiliation)
            }
        log.debug("Adding ${element.description} with callsign $callSign to engine at position $positionInSector")
        return addEntity(elementEntity)
    }

}