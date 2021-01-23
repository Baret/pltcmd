package de.gleex.pltcmd.model.signals.vision.builder

import de.gleex.pltcmd.model.signals.vision.Vision
import de.gleex.pltcmd.model.signals.vision.VisionPower
import de.gleex.pltcmd.model.world.WorldArea
import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.Terrain
import kotlin.random.Random

/**
 * An "empty vision signal" at an invalid location. Can be used as placeholder when no [WorldArea] can be
 * created.
 */
val initialVision = Vision(
    Coordinate.maximum,
    WorldArea(sortedSetOf(WorldTile(Coordinate.maximum, Terrain.random(Random)))),
    VisionPower.MIN
)