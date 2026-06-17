package de.gleex.pltcmd.game.application.graphics.terrain

import com.badlogic.gdx.Files
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import de.gleex.pltcmd.model.world.terrain.TerrainType
import kotlin.random.Random

/**
 * Manages the texture atlas containing terrain type graphics.
 */
class TerrainAssets {
    private val terrainAtlas by lazy { TextureAtlas(Gdx.files.getFileHandle("terrain/packed/terrain.atlas", Files.FileType.Classpath)) }

    /**
     * @param terrainType to load the [TextureRegion] for
     * @param seed to seed the random that is used to select one of multiple images
     * @return a [TextureRegion] from the [TextureAtlas] that contains the terrain [TerrainType] images.
     */
    fun textureFor(terrainType: TerrainType, seed: Long = System.currentTimeMillis()): TextureRegion {
        val regionName = when(terrainType) {
            TerrainType.GRASSLAND     -> "grasslands"
            TerrainType.FOREST        -> "forest"
            TerrainType.HILL          -> "hills"
            TerrainType.MOUNTAIN      -> "mountain"
            TerrainType.WATER_DEEP    -> "water_deep"
            TerrainType.WATER_SHALLOW -> "water_shallow"
        }
        val regions = terrainAtlas.findRegions(regionName)
        val index = Random(seed).nextInt(regions.size)
        return regions[index]
    }
}
