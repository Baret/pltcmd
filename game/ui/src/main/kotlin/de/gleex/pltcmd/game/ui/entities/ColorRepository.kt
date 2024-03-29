package de.gleex.pltcmd.game.ui.entities

import de.gleex.pltcmd.model.elements.Corps
import de.gleex.pltcmd.model.elements.ElementKind
import de.gleex.pltcmd.model.elements.Rung
import de.gleex.pltcmd.model.faction.Affiliation
import de.gleex.pltcmd.model.signals.core.SignalStrength
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import org.hexworks.zircon.api.color.TileColor

/**
 * Serves all colors needed in the game.
 */
object ColorRepository {

    private val ALPHA_BG = 99

    val HQ = TileColor.create(172, 244, 83)
    val HQ_TRANSPARENT = HQ.withAlpha(ALPHA_BG)

    val FRIENDLY = TileColor.create(42, 42, 254)
    val FRIENDLY_TRANSPARENT = FRIENDLY.withAlpha(ALPHA_BG)

    val HOSTILE = TileColor.create(251, 4, 33)
    val HOSTILE_TRANSPARENT = HOSTILE.withAlpha(ALPHA_BG)

    val UNKNOWN = TileColor.create(245, 241, 132)
    val UNKNOWN_TRANSPARENT = UNKNOWN.withAlpha(ALPHA_BG)

    /**
     * Returns the foreground and background color for the given [Affiliation] as [Pair] (forgreound, background)
     */
    fun forAffiliation(affiliation: Affiliation) = when (affiliation) {
        Affiliation.Friendly -> FRIENDLY to FRIENDLY_TRANSPARENT
        Affiliation.Hostile  -> HOSTILE to HOSTILE_TRANSPARENT
        Affiliation.Self     -> HQ to HQ_TRANSPARENT
        else                 -> UNKNOWN to UNKNOWN_TRANSPARENT
    }

    val GRID_COLOR = TileColor.defaultForegroundColor()
    val GRID_COLOR_HIGHLIGHT = TileColor.create(77, 77, 77)
    val COORDINATE_COLOR_HIGHLIGHT_X = TileColor.create(128, 102, 64) // brown
    val COORDINATE_COLOR_HIGHLIGHT_Y = TileColor.create(255, 128, 0) // orange

    val SIGNAL_FULL = TileColor.create(2, 238, 255, 130)
    val SIGNAL_FULL_HIGHLIGHT = TileColor.create(72, 255, 109)
    val SIGNAL_EMPTY = TileColor.create(255, 2, 112, SIGNAL_FULL.alpha)

    private val MAX_HEIGHT = TileColor.create(230, 230, 230, 254)

    /**
     * Creates a [TileColor] for the given [TerrainHeight] to be used as background color for tiles.
     */
    fun forHeight(height: TerrainHeight?): TileColor {
        if (height == null) return TileColor.defaultForegroundColor() // inverted background to foreground intentionally for highlighting
        return when (height) {
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
    }

    /**
     * Creates a [TileColor] for the given [TerrainType] to be used as foreground color for terrain tiles.
     */
    fun forType(type: TerrainType?): TileColor {
        if (type == null) return TileColor.defaultBackgroundColor() // inverted foreground to background intentionally for highlighting
        return when (type) {
            TerrainType.GRASSLAND     -> TileColor.create(95, 169, 51)
            TerrainType.FOREST        -> TileColor.create(35, 104, 10)
            TerrainType.HILL          -> TileColor.create(121, 77, 33)
            TerrainType.MOUNTAIN      -> TileColor.create(112, 107, 102)
            TerrainType.WATER_DEEP    -> TileColor.create(0, 0, 102)
            TerrainType.WATER_SHALLOW -> TileColor.create(40, 109, 222)
        }
    }

    /**
     * Creates a [TileColor] for the given [Corps] to highlight and differentiate it in the UI.
     */
    fun forCorps(corps: Corps): TileColor =
        when (corps) {
            Corps.Fighting       -> TileColor.create(217, 0, 0)
            Corps.Logistics      -> TileColor.create(217, 255, 102)
            Corps.CombatSupport  -> TileColor.create(255, 102, 51)
            Corps.Reconnaissance -> TileColor.create(255, 204, 51)
        }

    /**
     * Creates a [TileColor] for the given [ElementKind] to highlight and differentiate it in the UI.
     */
    fun forKind(kind: ElementKind): TileColor =
        when (kind) {
            ElementKind.Infantry           -> TileColor.create(0, 102, 0)
            ElementKind.MotorizedInfantry  -> TileColor.create(0, 204, 0)
            ElementKind.MechanizedInfantry -> TileColor.create(0, 217, 217)
            ElementKind.Armored            -> TileColor.create(102, 82, 51)
            ElementKind.Aerial             -> TileColor.create(0, 108, 217)
        }

    /**
     * Creates a [TileColor] for the given [Rung] to highlight and differentiate it in the UI.
     */
    fun forRung(rung: Rung): TileColor {
        val ratio = rung.ordinal.toDouble() / (Rung.entries.size - 1).toDouble()
        return TileColor
            .create(77, 77, 77)
            .interpolateTo(TileColor.create(191, 191, 191))
            .getColorAtRatio(ratio)
    }

    fun radioColor(signalStrength: SignalStrength): TileColor {
        val signalColor = SIGNAL_EMPTY.interpolateTo(SIGNAL_FULL).getColorAtRatio(signalStrength.asRatio())
        return if (signalStrength.isNone()) {
            signalColor.withAlpha(0)
        } else {
            signalColor
        }
    }
}
