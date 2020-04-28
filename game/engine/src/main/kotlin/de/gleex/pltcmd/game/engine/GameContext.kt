package de.gleex.pltcmd.game.engine

import de.gleex.pltcmd.model.world.WorldMap
import org.hexworks.amethyst.api.Context

data class GameContext(val currentTick: Int, val world: WorldMap) : Context
