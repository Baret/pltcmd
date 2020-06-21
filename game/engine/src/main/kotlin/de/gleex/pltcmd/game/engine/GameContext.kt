package de.gleex.pltcmd.game.engine

import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.currentPosition
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.Context
import org.hexworks.cobalt.datatypes.Maybe
import kotlin.random.Random

/**
 * The context needed for an update (aka "tick").
 *
 * @param currentTick the tick that is currently being simulated. May be used to trigger scheduled actions.
 */
data class GameContext(val currentTick: Int, val world: WorldMap, private val allElements: Set<ElementEntity>, val random: Random) : Context {

    fun findElementAt(location: Coordinate): Maybe<ElementEntity> {
        return Maybe.ofNullable(allElements.find { it.currentPosition == location })
    }

}
