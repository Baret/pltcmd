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
 * The game world contains all [GameBlock]s representing the map. It has a visible part and can scroll from [Sector] to sector.
 * It is also capable of translating [Coordinate]s to [Position3D] and vice versa.
 *
 * The intent of the GameWorld is to have fixed blocks, showing 3 layers (from bottom to top):
 *
 * - The terrain or fog of war, depending on their "revealed state" (coming later)
 * - The element marker for the unit/element on that position
 * - An optional overlay, currently used for debug purposes (radio signal strength), but might later be used by the player to "paint on the map".
 *
 */
class GameWorld(private val worldMap: WorldMap) :
        BaseGameArea<Tile, GameBlock>(
                initialVisibleSize = Size3D.create(Sector.TILE_COUNT, Sector.TILE_COUNT, 1),
                initialActualSize = Size3D.from2DSize(worldMap.size, 1),
                initialContents = mapOf(),
                projectionStrategy = TopDownProjectionStrategy()) {
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
            val block = GameBlock(it.terrain)
            setBlockAt(position, block)
        }
    }

    /** Returns the [Coordinate] of the [Tile] that is visible in the top left corner. */
    fun visibleTopLeftCoordinate(): Coordinate {
        return visibleOffset.toCoordinate()
    }

    /**
     * Make the given coordinate and ascending ones visible.
     * If the visible area has the size of a [Sector] and the origin of a [Sector] is given, the full sector will be visible.
     */
    fun scrollToCoordinate(bottomLeft: Coordinate) {
        val bottomLeftPos = bottomLeft.toPosition()
        val visibleTopLeftPos = bottomLeftPos.withRelativeY(-getMaxVisibleY())
        scrollTo(visibleTopLeftPos)
    }

    // model size
    private fun getMaxY() = actualSize.yLength - 1 // -1 because y is zero-based

    // ui size
    private fun getMaxVisibleY() = visibleSize.yLength - 1 // -1 because y is zero-based

    /**
     * Returns the difference between the origin of the world and the absolute origin (0, 0).
     * Used to translate the world map coordinates which start with an arbitrary value to our game area coordinates which start with (0, 0).
     */
    private fun WorldMap.getTopLeftOffset(): Position {
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

    /**
     * Adds the current [visibleOffset] to the given position to translate it to the corresponding [Position3D]
     * and returns the [GameBlock] at the location, if present.
     */
    fun fetchBlockAtVisiblePosition(position: Position) = fetchBlockAt(position.toVisiblePosition3D())

    /**
     * Returns the [Coordinate] at the currently visible position
     * @see fetchBlockAtVisiblePosition
     */
    fun coordinateAtVisiblePosition(position: Position) = position.toVisiblePosition3D().toCoordinate()

    private fun Position.toVisiblePosition3D() = visibleOffset.plus(this.to3DPosition(0))
}

