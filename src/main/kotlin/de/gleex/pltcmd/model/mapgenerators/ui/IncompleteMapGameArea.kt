package de.gleex.pltcmd.model.mapgenerators.ui

import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import org.hexworks.zircon.api.data.Position3D
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.data.Size3D
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.game.GameArea
import org.hexworks.zircon.api.game.base.BaseGameArea
import org.hexworks.zircon.internal.game.impl.TopDownProjectionStrategy

/**
 * A [GameArea] that shows a map with holes. It uses [IncompleteMapBlock] for that. The full map is visible and can be altered.
 */
class IncompleteMapGameArea(size: Size) :
        BaseGameArea<Tile, IncompleteMapBlock>(
                initialVisibleSize = Size3D.from2DSize(size, 1),
                initialActualSize = Size3D.from2DSize(size, 1),
                initialContents = initialContents(size.width, size.height),
                projectionStrategy = TopDownProjectionStrategy()) {

    companion object {
        private fun initialContents(width: Int, height: Int): Map<Position3D, IncompleteMapBlock> {
            val contents = mutableMapOf<Position3D, IncompleteMapBlock>()
            // fill area with voidness
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val position = Position3D.create(x, y, 0)
                    contents[position] = IncompleteMapBlock()
                }
            }
            return contents
        }
    }

    fun setBlock(position: Position3D, terrainHeight: TerrainHeight?, terrainType: TerrainType?) {
        blocks[position]?.setTerrain(terrainHeight, terrainType)
    }

}