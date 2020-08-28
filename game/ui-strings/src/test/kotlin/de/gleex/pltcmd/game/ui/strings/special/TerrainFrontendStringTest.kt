package de.gleex.pltcmd.game.ui.strings.special

import de.gleex.pltcmd.game.ui.strings.FrontendString
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import org.hexworks.cobalt.databinding.api.extension.toProperty

class TerrainFrontendStringTest : WordSpec({

    FrontendString.Format.values()
            .forEach { format ->
                TerrainHeight.values().forEach { height ->
                    TerrainType.values().forEach { type ->
                        "A terrain of type $type and height $height formatted with $format" should {
                            val expected: String = when(format) {
                                FrontendString.Format.FULL, FrontendString.Format.SIDEBAR -> "${height.fullString}, ${type.fullString}"
                                FrontendString.Format.SHORT5                              -> "${height.abbreviated}, ${type.abbreviated}"
                                FrontendString.Format.SHORT3                              -> "${height.abbreviated},${type.abbreviated.first()}"
                                FrontendString.Format.ICON                                -> "${type.abbreviated.first()}"
                            }
                            "result in the string '$expected'" {
                                val formattedTerrain = TerrainFrontendString(Terrain.of(type, height).toProperty(), format)
                                        .value
                                formattedTerrain shouldBe expected
                            }
                        }
                    }
                }
            }

})

private val TerrainType.fullString: String
    get() = when (this) {
        TerrainType.GRASSLAND     -> "Grassland"
        TerrainType.FOREST        -> "Forest"
        TerrainType.HILL          -> "Hills"
        TerrainType.MOUNTAIN      -> "Mountain"
        TerrainType.WATER_DEEP    -> "Deep water"
        TerrainType.WATER_SHALLOW -> "Shallow water"
    }

private val TerrainType.abbreviated: String
    get() = when (this) {
        TerrainType.GRASSLAND     -> "GL"
        TerrainType.FOREST        -> "FO"
        TerrainType.HILL          -> "HI"
        TerrainType.MOUNTAIN      -> "MO"
        TerrainType.WATER_DEEP    -> "DW"
        TerrainType.WATER_SHALLOW -> "SW"
    }

private val TerrainHeight.fullString: String
        get() = when(this) {
            TerrainHeight.ONE   -> "01/10"
            TerrainHeight.TWO   -> "02/10"
            TerrainHeight.THREE -> "03/10"
            TerrainHeight.FOUR  -> "04/10"
            TerrainHeight.FIVE  -> "05/10"
            TerrainHeight.SIX   -> "06/10"
            TerrainHeight.SEVEN -> "07/10"
            TerrainHeight.EIGHT -> "08/10"
            TerrainHeight.NINE  -> "09/10"
            TerrainHeight.TEN   -> "10/10"
        }

private val TerrainHeight.abbreviated: String
    get() = when(this) {
        TerrainHeight.ONE   -> "1"
        TerrainHeight.TWO   -> "2"
        TerrainHeight.THREE -> "3"
        TerrainHeight.FOUR  -> "4"
        TerrainHeight.FIVE  -> "5"
        TerrainHeight.SIX   -> "6"
        TerrainHeight.SEVEN -> "7"
        TerrainHeight.EIGHT -> "8"
        TerrainHeight.NINE  -> "9"
        TerrainHeight.TEN   -> "^"
    }