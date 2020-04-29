package de.gleex.pltcmd.game.engine

import de.gleex.pltcmd.game.engine.entities.ElementEntity
import de.gleex.pltcmd.game.engine.entities.ElementType
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.elements.Element
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap
import org.hexworks.amethyst.api.Engine
import org.hexworks.amethyst.api.entity.Entity
import kotlin.random.Random

data class Game(val engine: Engine<GameContext>, val world: WorldMap) {
    /**
     * Adds the given entity to the engine and returns it to make chained calls possible.
     */
    fun addEntity(entity: Entity<ElementType, GameContext>): Entity<ElementType, GameContext> {
        engine.addEntity(entity)
        return entity
    }

    /**
     * Adds a new element in the given sector and returns it. If it was not possible to add the returned value is null.
     */
    fun addElementInSector(sector: Sector): ElementEntity? {
        val positionInSector = sector.randomCoordinate()
        val callSign = CallSign("Element ${Random.nextInt(999_999)}")
        val element = ElementEntity(Element(callSign, emptySet()), positionInSector)
        engine.addEntity(element)
        return element
    }
}