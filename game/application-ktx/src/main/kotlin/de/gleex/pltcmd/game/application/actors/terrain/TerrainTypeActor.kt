package de.gleex.pltcmd.game.application.actors.terrain

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Scaling
import de.gleex.pltcmd.game.application.graphics.Assets
import de.gleex.pltcmd.model.world.terrain.TerrainType

/**
 * An [Image] actor that just displays the image for the given [TerrainType].
 */
class TerrainTypeActor(terrainType: TerrainType) : Image(Assets.terrain.textureFor(terrainType)) {
    init {
        setSize(1f, 1f)
        setScaling(Scaling.fill)
    }
}