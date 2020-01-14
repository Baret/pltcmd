package de.gleex.pltcmd.game

import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import org.hexworks.zircon.api.TileColors
import org.hexworks.zircon.api.color.TileColor

/**
 * Serves all colors needed in the game.
 */
object ColorRepository {

    val GRID_COLOR = TileColors.defaultForegroundColor()
    val GRID_COLOR_HIGHLIGHT = TileColors.create(77, 77, 77)

    private val MAX_HEIGHT = TileColors.create(230, 230, 230)

    /**
     * Creates a [TileColor] for the given [TerrainHeight] to be used as background color for tiles.
     */
    fun forHeight(height: TerrainHeight): TileColor = when(height) {
        TerrainHeight.ONE -> MAX_HEIGHT.darkenByPercent(0.9)
        TerrainHeight.TWO -> MAX_HEIGHT.darkenByPercent(0.8)
        TerrainHeight.THREE -> MAX_HEIGHT.darkenByPercent(0.7)
        TerrainHeight.FOUR -> MAX_HEIGHT.darkenByPercent(0.6)
        TerrainHeight.FIVE -> MAX_HEIGHT.darkenByPercent(0.5)
        TerrainHeight.SIX -> MAX_HEIGHT.darkenByPercent(0.4)
        TerrainHeight.SEVEN -> MAX_HEIGHT.darkenByPercent(0.3)
        TerrainHeight.EIGHT -> MAX_HEIGHT.darkenByPercent(0.2)
        TerrainHeight.NINE -> MAX_HEIGHT.darkenByPercent(0.1)
        TerrainHeight.TEN -> MAX_HEIGHT
    }

    /**
     * Creates a [TileColor] for the given [TerrainType] to be used as foreground color for terrain tiles.
     */
    fun forType(type: TerrainType): TileColor = when(type) {
        TerrainType.GRASSLAND -> TileColors.create(95, 169, 51)
        TerrainType.FOREST -> TileColors.create(35, 104, 10)
        TerrainType.HILL -> TileColors.create(121, 77, 33)
        TerrainType.MOUNTAIN -> TileColors.create(112, 107, 102)
    }

    fun radioColor(signalStrength: Double): TileColor {
        println("Creating radio color for signal $signalStrength with alpha ${(255 * signalStrength).toInt()}")
        return TileColors.create(200, 43, 43, (255 * signalStrength).toInt())
    }
}
