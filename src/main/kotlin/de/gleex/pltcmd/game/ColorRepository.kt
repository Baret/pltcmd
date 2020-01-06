package de.gleex.pltcmd.game

import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import org.hexworks.zircon.api.TileColors
import org.hexworks.zircon.api.color.TileColor

/**
 * Serves all colors needed in the game.
 */
object ColorRepository {

    private val MAX_HEIGHT = TileColors.fromString("#C8C8C8")

    /**
     * Creates a [TileColor] for the given [TerrainHeight] to be used as background color for tiles.
     */
    fun forHeight(height: TerrainHeight): TileColor = when(height) {
        TerrainHeight.ONE -> MAX_HEIGHT.shade(0.1)
        TerrainHeight.TWO -> MAX_HEIGHT.shade(0.2)
        TerrainHeight.THREE -> MAX_HEIGHT.shade(0.3)
        TerrainHeight.FOUR -> MAX_HEIGHT.shade(0.4)
        TerrainHeight.FIVE -> MAX_HEIGHT.shade(0.5)
        TerrainHeight.SIX -> MAX_HEIGHT.shade(0.6)
        TerrainHeight.SEVEN -> MAX_HEIGHT.shade(0.7)
        TerrainHeight.EIGHT -> MAX_HEIGHT.shade(0.8)
        TerrainHeight.NINE -> MAX_HEIGHT.shade(0.9)
        TerrainHeight.TEN -> MAX_HEIGHT
    }

    /**
     * Creates a [TileColor] for the given [TerrainType] to be used as foreground color for terrain tiles.
     */
    fun forType(type: TerrainType): TileColor = when(type) {
        TerrainType.GRASSLAND -> TileColor.create(200, 200 ,200 ,0)
        TerrainType.FOREST -> TileColor.create(100, 99 ,255 ,0)
        TerrainType.HILL -> TileColor.create(50, 144 ,200 ,0)
        TerrainType.MOUNTAIN -> TileColor.create(200, 50 ,92 ,0)
    }
}
