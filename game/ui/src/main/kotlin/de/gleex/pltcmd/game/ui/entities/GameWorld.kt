package de.gleex.pltcmd.game.ui.entities

import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.model.faction.Faction
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import kotlinx.collections.immutable.persistentMapOf
import mu.KotlinLogging
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Position3D
import org.hexworks.zircon.api.data.Size3D
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.game.base.BaseGameArea

private val log = KotlinLogging.logger {}

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
 * @param factionViewToPresent used to color element markers. See [ColorRepository.forAffiliation].
 */
class GameWorld(private val worldMap: WorldMap, private val factionViewToPresent: Faction) :
    BaseGameArea<Tile, GameBlock>(
        initialVisibleSize = Size3D.create(Sector.TILE_COUNT, Sector.TILE_COUNT, 1),
        initialActualSize = Size3D.create(worldMap.width, worldMap.height, 1),
        initialContents = persistentMapOf(),
        initialFilters = emptyList()
    ) {

    private val topLeftOffset: Position
        get() = worldMap.getTopLeftOffset()

    init {
        worldMap.allTiles.forEach(::createTile)
        log.debug { "Created GameWorld. Visible size = $visibleSize" }
    }

    private fun createTile(tile: WorldTile) {
        val position = tile.coordinate.toPosition()
        val block = GameBlock(tile.terrain)
        setBlockAt(position, block)
    }

    /** adds a marker to the map which is synced with the position of the given element */
    fun trackUnit(element: ElementEntity) {
        element.showOnMap()
        element.position.onChange {
            it.oldValue.hideUnit()
            element.showOnMap()
        }
        element onDefeat { element.hide() }
    }

    /** adds a friendly marker for this FOB */
    fun showBase(base: FOBEntity) {
        val affiliation = base.affiliationTo(factionViewToPresent)
        val fobTile = TileRepository.createFobTile(affiliation)
        base.currentPosition.setUnit(fobTile)
    }

    private fun ElementEntity.showOnMap() {
        val affiliation = affiliationTo(factionViewToPresent)
        val elementTile = TileRepository.Elements.marker(element, affiliation)
        currentPosition.setUnit(elementTile)
    }

    private fun Coordinate.setUnit(unitTile: Tile) {
        fetchBlockAt(this)?.setUnit(unitTile)
    }

    private fun ElementEntity.hide() {
        currentPosition.hideUnit()
    }

    private fun Coordinate.hideUnit() {
        fetchBlockAt(this)?.resetUnit()
    }

    /** Returns the [Coordinate] of the [Tile] that is visible in the top left corner. */
    fun visibleTopLeftCoordinate(): Coordinate {
        return visibleOffset.toCoordinate()
    }

    // model size
    private fun getMaxY() = actualSize.yLength - 1 // -1 because y is zero-based

    /**
     * Returns the difference between the origin of the world and the absolute origin (0, 0).
     * Used to translate the world map coordinates which start with an arbitrary value to our game area coordinates which start with (0, 0).
     */
    private fun WorldMap.getTopLeftOffset(): Position {
        // use origin of world (minimal numeric value of coordinate) to calculate the offset
        return Position.create(-origin.eastingFromLeft, -origin.northingFromBottom)
    }

    /**
     * @return the [GameBlock] at the given [Coordinate] if it is contained in this world.
     */
    fun fetchBlockAt(coordinate: Coordinate): GameBlock? =
        fetchBlockAtOrNull(coordinate.toPosition())

    private fun Coordinate.toPosition(): Position3D {
        // translate to 0,0 based grid then invert y axis
        val translatedX = eastingFromLeft + topLeftOffset.x
        val translatedY = northingFromBottom + topLeftOffset.y
        return Position3D.create(translatedX, getMaxY() - translatedY, 0)
    }

    private fun Position3D.toCoordinate(): Coordinate {
        // invert y axis then translate to map origin based grid
        val invertedY = getMaxY() - y
        return Coordinate(x - topLeftOffset.x, invertedY - topLeftOffset.y)
    }

    /**
     * Adds the current [visibleOffset] to the given position to translate it to the corresponding [Position3D]
     * and returns the [GameBlock] at the location, if present.
     */
    fun fetchBlockAtVisiblePosition(position: Position) = fetchBlockAtOrNull(position.toVisiblePosition3D())

    /**
     * Returns the [Coordinate] at the currently visible position
     * @see fetchBlockAtVisiblePosition
     */
    fun coordinateAtVisiblePosition(position: Position) =
        position
            .toVisiblePosition3D()
            .toCoordinate()

    private fun Position.toVisiblePosition3D() = visibleOffset.plus(this.toPosition3D(0))
}

