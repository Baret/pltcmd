package de.gleex.pltcmd.model.mapgenerators.ui

import de.gleex.pltcmd.model.terrain.Terrain
import org.hexworks.zircon.api.data.Position3D
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.data.Size3D
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.game.base.BaseGameArea
import org.hexworks.zircon.internal.game.impl.TopDownProjectionStrategy

class IncompleteMapGameArea(size: Size) :
        BaseGameArea<Tile, IncompleteMapBlock>(
                initialVisibleSize = Size3D.from2DSize(size, 1),
                initialActualSize = Size3D.from2DSize(size, 1),
                initialContents = mapOf(),
                projectionStrategy = TopDownProjectionStrategy()) {

    init {
        // fill area with voidness
        for (y in 0 until size.width) {
            for (x in 0 until size.height) {
                setBlock(x, y, null)
            }
        }
    }

    fun setBlock(x: Int, y: Int, terrain: Terrain?) {
        val position = Position3D.create(x, y, 0)
        setBlock(position, terrain)
    }

    fun setBlock(position: Position3D, terrain: Terrain?) {
        val block = IncompleteMapBlock(terrain)
        setBlockAt(position, block)
    }

}