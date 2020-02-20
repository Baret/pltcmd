package de.gleex.pltcmd.game

import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import org.hexworks.zircon.api.color.TileColor

/**
 * Serves all colors needed in the game.
 */
object ColorRepository {

    val FRIENDLY = TileColor.create(42, 42, 254)
    val FRIENDLY_TRANSPARENT = TileColor.create(42, 42, 254, 99)

    val TRANSPARENT = TileColor.transparent()

    val GRID_COLOR = TileColor.defaultForegroundColor()
    val GRID_COLOR_HIGHLIGHT = TileColor.create(77, 77, 77)
    val COORDINATE_COLOR_HIGHLIGHT_X = TileColor.create(128, 102, 64) // brown
    val COORDINATE_COLOR_HIGHLIGHT_Y = TileColor.create(255, 128, 0) // orange

    private val SIGNAL_FULL = TileColor.create(0, 255, 0, 130)
    private val SIGNAL_EMPTY = TileColor.create(255, 0, 0, SIGNAL_FULL.alpha)

    private val MAX_HEIGHT = TileColor.create(230, 230, 230, 254)

    /**
     * Creates a [TileColor] for the given [TerrainHeight] to be used as background color for tiles.
     */
    fun forHeight(height: TerrainHeight): TileColor = when (height) {
        TerrainHeight.ONE   -> MAX_HEIGHT.darkenByPercent(0.9)
        TerrainHeight.TWO   -> MAX_HEIGHT.darkenByPercent(0.8)
        TerrainHeight.THREE -> MAX_HEIGHT.darkenByPercent(0.7)
        TerrainHeight.FOUR  -> MAX_HEIGHT.darkenByPercent(0.6)
        TerrainHeight.FIVE  -> MAX_HEIGHT.darkenByPercent(0.5)
        TerrainHeight.SIX   -> MAX_HEIGHT.darkenByPercent(0.4)
        TerrainHeight.SEVEN -> MAX_HEIGHT.darkenByPercent(0.3)
        TerrainHeight.EIGHT -> MAX_HEIGHT.darkenByPercent(0.2)
        TerrainHeight.NINE  -> MAX_HEIGHT.darkenByPercent(0.1)
        TerrainHeight.TEN   -> MAX_HEIGHT
    }

    /**
     * Creates a [TileColor] for the given [TerrainType] to be used as foreground color for terrain tiles.
     */
    fun forType(type: TerrainType): TileColor = when (type) {
        TerrainType.GRASSLAND -> TileColor.create(95, 169, 51)
        TerrainType.FOREST    -> TileColor.create(35, 104, 10)
        TerrainType.HILL      -> TileColor.create(121, 77, 33)
        TerrainType.MOUNTAIN  -> TileColor.create(112, 107, 102)
        TerrainType.WATER_DEEP -> TileColor.create(0, 0, 102)
        TerrainType.WATER_SHALLOW -> TileColor.create(40, 109, 222)
    }

    fun radioColor(signalStrength: Double): TileColor {
        val c = TileColor.create(
                red = SIGNAL_EMPTY.red + ((SIGNAL_FULL.red - SIGNAL_EMPTY.red) * signalStrength).toInt(),
                green = SIGNAL_EMPTY.green + ((SIGNAL_FULL.green - SIGNAL_EMPTY.green) * signalStrength).toInt(),
                blue = SIGNAL_EMPTY.blue + ((SIGNAL_FULL.blue - SIGNAL_EMPTY.blue) * signalStrength).toInt(),
                alpha = if(signalStrength > 0.0) SIGNAL_FULL.alpha else 0
        )
        println("$signalStrength% between $SIGNAL_FULL and $SIGNAL_EMPTY = $c")
        return c
    }
}
