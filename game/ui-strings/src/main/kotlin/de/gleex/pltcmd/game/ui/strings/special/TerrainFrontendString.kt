package de.gleex.pltcmd.game.ui.strings.special

import de.gleex.pltcmd.game.ui.strings.DefaultFrontendString
import de.gleex.pltcmd.game.ui.strings.FrontendString
import de.gleex.pltcmd.game.ui.strings.FrontendString.Format.*
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import de.gleex.pltcmd.model.world.terrain.TerrainType.*
import org.hexworks.cobalt.databinding.api.value.ObservableValue

/**
 * A [FrontendString] for [Terrain].
 */
class TerrainFrontendString(
        observableTerrain: ObservableValue<Terrain>,
        format: FrontendString.Format
) : DefaultFrontendString<Terrain>(observableTerrain, format) {

    override fun FrontendString.Format.invoke(objectToTransform: Terrain): String {
        val type = objectToTransform.type
        val height = objectToTransform.height
        return when(this) {
            ICON    -> type.short.substring(0..0)
            SHORT3  -> "${height.short},${type.short.first()}"
            SHORT5   -> "${height.short}, ${type.short}"
            SIDEBAR,
                FULL     -> "${height.formatted}, ${type.formatted}"
        }
    }

    private val TerrainType.formatted: String
        get() = when (this) {
            GRASSLAND,
                FOREST,
                MOUNTAIN      -> name.toLowerCase().capitalize()
            HILL          -> "Hills"
            WATER_DEEP    -> "Deep water"
            WATER_SHALLOW -> "Shallow water"
        }

    private val TerrainType.short: String
        get() {
            val allCaps = when(this) {
                FOREST,
                    HILL,
                    MOUNTAIN  -> name.toUpperCase()
                GRASSLAND     -> "GL"
                WATER_DEEP    -> "DW"
                WATER_SHALLOW -> "SW"
            }
            return allCaps.substring(0..1)
        }

    private val TerrainHeight.formatted: String
        get() = "%02d/10".format(value)
        }

    private val TerrainHeight.short: String
        get() = if(value <= 9) {
            "$value"
        } else {
            "^"
}
