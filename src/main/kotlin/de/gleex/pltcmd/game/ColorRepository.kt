package de.gleex.pltcmd.game

import de.gleex.pltcmd.model.terrain.TerrainHeight
import org.hexworks.zircon.api.TileColors
import org.hexworks.zircon.api.color.TileColor

/**
 * Serves all colors needed in the game.
 */
object ColorRepository {

    private val MAX_HEIGHT = TileColors.create(200, 200, 200, 254)

    /**
     * Creates a [TileColor] for the given [TerrainHeight] to be used as background color for tiles.
     */
    fun heihgtColor(height: TerrainHeight): TileColor = when(height) {
        TerrainHeight.ONE -> MAX_HEIGHT.lightenByPercent(0.1)
        TerrainHeight.TWO -> MAX_HEIGHT.lightenByPercent(0.2)
        TerrainHeight.THREE -> MAX_HEIGHT.lightenByPercent(0.3)
        TerrainHeight.FOUR -> MAX_HEIGHT.lightenByPercent(0.4)
        TerrainHeight.FIVE -> MAX_HEIGHT.lightenByPercent(0.5)
        TerrainHeight.SIX -> MAX_HEIGHT.lightenByPercent(0.6)
        TerrainHeight.SEVEN -> MAX_HEIGHT.lightenByPercent(0.7)
        TerrainHeight.EIGHT -> MAX_HEIGHT.lightenByPercent(0.8)
        TerrainHeight.NINE -> MAX_HEIGHT.lightenByPercent(0.9)
        TerrainHeight.TEN -> MAX_HEIGHT
    }
}
