package de.gleex.pltcmd.game

import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap
import org.hexworks.cobalt.logging.api.LoggerFactory
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Position3D
import org.hexworks.zircon.api.data.Size3D
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.game.base.BaseGameArea
import org.hexworks.zircon.internal.game.impl.TopDownProjectionStrategy

/**
 * The game world contains all [MapBlock]s representing the map. It has a visible part and can scroll from [Sector] to sector.
 * It is also capable of translating [Coordinate]s to [Position3D] and vice versa.
 */
class GameWorld(private val worldMap: WorldMap):
        BaseGameArea<Tile, MapBlock>(
                initialVisibleSize = Size3D.create(Sector.TILE_COUNT, Sector.TILE_COUNT, 1),
                initialActualSize = Size3D.from2DSize(worldMap.size, 1),
                initialContents = mapOf(),
                projectionStrategy = TopDownProjectionStrategy())
{
    /**
     * Returns all currently visible blocks.
     *
     * @see GameWorld.fetchBlocksAt
     */
    val visibleBlocks
        get() = fetchBlocksAt(visibleOffset, visibleSize)

    companion object {
        private val log = LoggerFactory.getLogger(GameWorld::class)
    }

    private val topLeftOffset: Position
            get() = worldMap.getTopLeftOffset()

    init {
        worldMap.sectors.forEach(::putSector)
        log.debug("Created GameWorld with ${worldMap.sectors.size} sectors. Visible size = $visibleSize")
    }

    private fun putSector(sector: Sector) {
        sector.tiles.forEach {
            val position = it.coordinate.toPosition()
            val block = MapBlock(it.terrain)
            setBlockAt(position, block)
        }
    }

    /** Returns the [Coordinate] of the [Tile] that is visible in the top left corner. */
    fun visibleTopLeftCoordinate(): Coordinate {
        return visibleOffset.toCoordinate()
    }

    fun scrollToCoordinate(bottomLeft: Coordinate) {
        val bottomLeftPos = bottomLeft.toPosition()
        val visibleTopLeftPos = bottomLeftPos.withRelativeY(-getMaxVisibleY())
        scrollTo(visibleTopLeftPos)
    }

    // model size
    private fun getMaxY() = actualSize.yLength - 1 // -1 because y is zero-based

    // ui size
    private fun getMaxVisibleY() = visibleSize.yLength - 1 // -1 because y is zero-based

    private fun WorldMap.getTopLeftOffset(): Position {
        // we translate the world map coordinates which start with an arbitrary value to our game area coordinates which start with (0, 0)
        // use origin of world (minimal numeric value of coordinate) to calculate the offset
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
