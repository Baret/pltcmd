package de.gleex.pltcmd.game.application.actors.terrain

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Group
import de.gleex.pltcmd.model.world.WorldTile

class WorldTileActor(private val tile: WorldTile): Group() {
    init {
        addActor(TerrainTypeActor(tile.type))
        addActor(TerrainHeightActor(tile.height))
        addActor(CoordinateTileActor(tile.coordinate, tile.terrain.height))
    }

    override fun addAction(action: Action?) {
        super.addAction(action)
    }
}