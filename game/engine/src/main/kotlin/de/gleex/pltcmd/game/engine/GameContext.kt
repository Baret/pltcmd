package de.gleex.pltcmd.game.engine

import de.gleex.pltcmd.model.world.WorldMap
import org.hexworks.amethyst.api.Context
import kotlin.random.Random

/**
 * The context needed for an update (aka "tick").
 *
 * @param currentTick the tick that is currently being simulated. May be used to trigger scheduled actions.
 */
data class GameContext(val currentTick: Int, val world: WorldMap, val random: Random) : Context
