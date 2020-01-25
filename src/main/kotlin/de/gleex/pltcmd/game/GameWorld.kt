package de.gleex.pltcmd.game

import de.gleex.pltcmd.model.terrain.Terrain
import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap
import org.hexworks.cobalt.logging.api.LoggerFactory
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.builder.game.GameAreaBuilder
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.data.impl.Position3D
import org.hexworks.zircon.api.game.GameArea

class GameWorld(worldMap: WorldMap) : GameArea<Tile, MapBlock> by GameAreaBuilder.newBuilder<Tile, MapBlock>().
        withActualSize(Sizes.from2DTo3D(worldMap.size, 1)).
        withVisibleSize(Sizes.create3DSize(Sector.TILE_COUNT, Sector.TILE_COUNT, 1)).
        withLayersPerBlock(MapBlock.LAYERS_PER_BLOCK).
        withDefaultBlock(MapBlock(Terrain(TerrainType.GRASSLAND, TerrainHeight.ONE))).
        build()
{
    companion object {
        private val log = LoggerFactory.getLogger(GameWorld::class)
    }

    // TODO only works if world is immutable
    private val topLeftOffset = worldMap.getTopLeftOffset()

    init {
        toBlocks(worldMap)
        log.debug("Created GameWorld with ${worldMap.sectors.size} sectors. Visible size = ${visibleSize()}")
    }

    private fun toBlocks(worldMap: WorldMap) {
        worldMap.sectors.forEach { sector ->
            sector.tiles.forEach {
                val position = it.coordinate.toPosition()
                val block = MapBlock(it.terrain)
                setBlockAt(position, block)
            }
        }
    }

    /** Returns the [Coordinate] of the [Tile] that is visible in the top left corner. */
    fun visibleTopLeftCoordinate(): Coordinate {
        return visibleOffset().toCoordinate()
    }

    fun scrollToCoordinate(bottomLeft: Coordinate) {
        val bottomLeftPos = bottomLeft.toPosition()
        val visibleTopLeftPos = bottomLeftPos.withRelativeY(-getMaxVisibleY())
        scrollTo3DPosition(visibleTopLeftPos)
    }

    // model size
    private fun getMaxY() = actualSize().yLength - 1 // -1 because y is zero-based

    // ui size
    private fun getMaxVisibleY() = visibleSize().yLength - 1 // -1 because y is zero-based

    private fun WorldMap.getTopLeftOffset(): Position {
        // we translate the world map coordinates which start with an arbitrary value to our game area coordinates which start with (0, 0)
        // use minimal numeric value of coordinate to calculate the offset
        return Position.create(-origin.eastingFromLeft, -origin.northingFromBottom)
    }

    private fun Coordinate.toPosition(): Position3D {
        val translatedPos = Position.create(eastingFromLeft, northingFromBottom) + topLeftOffset
        // invert y axis
        return Position3D.from2DPosition(translatedPos.withY(getMaxY() - translatedPos.y))
    }

    private fun Position3D.toCoordinate(): Coordinate {
        val translatedPos = to2DPosition() - topLeftOffset
        // invert y axis
        return Coordinate(translatedPos.x, translatedPos.y + getMaxY())
    }

}
