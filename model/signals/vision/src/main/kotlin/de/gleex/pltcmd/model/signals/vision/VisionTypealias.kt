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
 * An "empty vision signal" at an invalid location. Can be used as placeholder when no [WorldArea] can be
 * created.
 */
val initialVision = Vision(
        Coordinate.maximum,
        WorldArea(sortedSetOf(WorldTile(Coordinate.maximum, Terrain.random(Random)))),
        VisionPower.MIN
)
