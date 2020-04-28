package de.gleex.pltcmd.game.engine

import de.gleex.pltcmd.model.world.WorldMap
import org.hexworks.amethyst.api.Engine

data class Game(val engine: Engine<GameContext>, val world: WorldMap)