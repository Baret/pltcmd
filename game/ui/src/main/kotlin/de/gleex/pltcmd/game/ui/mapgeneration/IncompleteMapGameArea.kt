package de.gleex.pltcmd.game.ui.mapgeneration

import de.gleex.pltcmd.model.world.coordinate.MainCoordinate
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import kotlinx.collections.immutable.toPersistentMap
import org.hexworks.zircon.api.data.Position3D
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.data.Size3D
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.game.GameArea
import org.hexworks.zircon.api.game.base.BaseGameArea
import org.hexworks.zircon.api.modifier.BorderPosition

/**
 * A [GameArea] that shows a map with holes. It uses [IncompleteMapBlock] for that.
 * The full map is visible and can be altered.
 */
class IncompleteMapGameArea(size: Size, worldSizeInTiles: Size) :
        BaseGameArea<Tile, IncompleteMapBlock>(
                initialVisibleSize = Size3D.from2DSize(size, 1),
                initialActualSize = Size3D.from2DSize(size, 1),
                initialContents = BlocksWithGrid(size, worldSizeInTiles).create(),
                initialFilters = emptyList()) {

    fun updateBlock(position: Position3D, terrainHeight: TerrainHeight?, terrainType: TerrainType?) {
        blocks[position]?.setTerrain(terrainHeight, terrainType)
    }

}

/** Creates [IncompleteMapBlock]s to fill the given [size]. Blocks will show a grid for the main coordinates in [worldSizeInTiles]. */
private data class BlocksWithGrid(private val size: Size, private val worldSizeInTiles: Size) {
    private val tilesPerMainCoordinateX = size.width / (worldSizeInTiles.width.toDouble() / MainCoordinate.TILE_COUNT)
    private val tilesPerMainCoordinateY = size.height / (worldSizeInTiles.height.toDouble() / MainCoordinate.TILE_COUNT)

    fun create() = size.fetchPositions()
            .map { it.to3DPosition(0) }
            .associateWith {
                IncompleteMapBlock(it.getBorders())
            }
            .toPersistentMap()

    private fun Position3D.getBorders(): Set<BorderPosition> {
        return mutableSetOf<BorderPosition>().apply {
            if (isAtHorizontalGrid) add(BorderPosition.LEFT)
            if (isAtVerticalGrid)   add(BorderPosition.TOP)
        }
    }

    private val Position3D.isAtHorizontalGrid: Boolean
        get() = (x % tilesPerMainCoordinateX) < 1.0

    private val Position3D.isAtVerticalGrid: Boolean
        get() = (y % tilesPerMainCoordinateY) < 1.0

}
