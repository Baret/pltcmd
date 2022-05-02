package de.gleex.pltcmd.game.ui.mapgeneration

import de.gleex.pltcmd.model.world.coordinate.MainCoordinate
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import kotlinx.collections.immutable.toPersistentMap
import org.hexworks.zircon.api.data.*
import org.hexworks.zircon.api.game.GameArea
import org.hexworks.zircon.api.game.base.BaseGameArea
import org.hexworks.zircon.api.modifier.BorderPosition

/**
 * A [GameArea] that shows a map with holes for ungenerated terrain. It uses [IncompleteMapBlock] for that.
 * The full map is visible and can be altered.
 *
 * This game area creates a 3D model of the map which means it puts the blocks at [TerrainHeight].
 * By that it is possible to display this game area in a projection mode different than TOP_DOWN.
 */
class IncompleteMapGameArea(size: Size, worldSizeInTiles: Size) :
        BaseGameArea<Tile, IncompleteMapBlock>(
                initialVisibleSize = Size3D.from2DSize(size, BLOCKS_HEIGHT),
                initialActualSize = Size3D.from2DSize(size, BLOCKS_HEIGHT),
                initialContents = BlocksWithGrid(size, worldSizeInTiles).create(),
                initialFilters = emptyList()) {

    companion object {
        private val BLOCKS_HEIGHT = TerrainHeight.values().size
        private const val Z_LEVEL_DEFAULT: Int = 0

        val Position.verticalPositions: Sequence<Position3D>
            get() = sequence {
                repeat(BLOCKS_HEIGHT) { zIndex ->
                    yield(toPosition3D(zIndex))
                }
            }
    }

    init {
        size.fetchPositions()
                .forEach { it.setTerrain(null, null) }
    }

    fun updateBlock(position: Position, terrainHeight: TerrainHeight?, terrainType: TerrainType?) {
        position.setTerrain(terrainHeight, terrainType)
    }

    private fun Position.setTerrain(terrainHeight: TerrainHeight?, terrainType: TerrainType?) {
        val terrainZLevel = terrainHeight?.ordinal ?: Z_LEVEL_DEFAULT
        verticalPositions
                .forEach { position3D ->
                    fetchBlockAtOrNull(position3D)
                            ?.let { block ->
                                when {
                                    position3D.z == terrainZLevel -> block.setTerrain(terrainHeight, terrainType)
                                    position3D.z < terrainZLevel -> block.setTerrain(TerrainHeight.ofValue(position3D.z), null)
                                    position3D.z > terrainZLevel -> block.setAir()
                                }
                            }
                }
    }

    /** Creates [IncompleteMapBlock]s to fill the given [size]. Blocks will show a grid for the main coordinates in [worldSizeInTiles]. */
    private data class BlocksWithGrid(private val size: Size, private val worldSizeInTiles: Size) {
        private val tilesPerMainCoordinateX = size.width / (worldSizeInTiles.width.toDouble() / MainCoordinate.TILE_COUNT)
        private val tilesPerMainCoordinateY = size.height / (worldSizeInTiles.height.toDouble() / MainCoordinate.TILE_COUNT)

        fun create() = size.fetchPositions()
                .flatMap { it.verticalPositions }
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

}
