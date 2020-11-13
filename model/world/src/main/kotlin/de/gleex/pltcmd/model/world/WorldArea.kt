package de.gleex.pltcmd.model.world

import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import java.util.*

open class WorldArea(val tiles: SortedSet<WorldTile>): CoordinateArea(tiles.map { it.coordinate }.toSortedSet()) {
}