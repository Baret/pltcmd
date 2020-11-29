package de.gleex.pltcmd.model.signals.vision

import de.gleex.pltcmd.model.signals.core.Signal
import de.gleex.pltcmd.model.world.WorldArea
import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.Terrain
import kotlin.random.Random

/**
 * A [Signal] based on [VisionPower].
 */
typealias Vision = Signal<VisionPower>

/**
 * Utility method to create an "empty" signal that has only a range of the given coordinate.
 */
fun noVisionAt(location: Coordinate): Vision =
        Vision(
                location,
                WorldArea(sortedSetOf(WorldTile(location, Terrain.random(Random)))),
                VisionPower.MIN
        )