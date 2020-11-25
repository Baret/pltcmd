package de.gleex.pltcmd.game.engine

import de.gleex.pltcmd.game.engine.entities.EntitySet
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.ticks.TickId
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.Context
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.datatypes.Maybe
import kotlin.random.Random

/**
 * The context needed for an update (aka "tick").
 *
 * @param currentTick the tick that is currently being simulated. May be used to trigger scheduled actions.
 * @param world the world map.
 * @param entities all entities currently present in the game. May be used to query for specific entities.
 * @param random to be used to generate random numbers.
 */
data class GameContext(
        val currentTick: TickId,
        val world: WorldMap,
        val entities: EntitySet<EntityType>,
        val random: Random
) : Context {

    /**
     * Shorthand version for [EntitySet.firstElementAt] on [entities].
     */
    fun firstElementAt(location: Coordinate): Maybe<ElementEntity> =
            entities.firstElementAt(location)

}
