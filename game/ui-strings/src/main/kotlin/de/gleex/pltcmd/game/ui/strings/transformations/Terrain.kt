package de.gleex.pltcmd.game.ui.strings.transformations

import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.game.ui.strings.Transformation
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import org.hexworks.zircon.api.graphics.Symbols

internal val terrainTransformation: Transformation<Terrain> = { format ->
    when(format) {
        Format.ICON    -> type.terrainTypeTransformation(format)
        Format.SHORT3  -> "${height.terrainHeightTransformation(format)},${type.terrainTypeTransformation(format)}"
        Format.SHORT5  -> "${height.terrainHeightTransformation(Format.ICON)},${type.terrainTypeTransformation(Format.SHORT3)}"
        Format.SIDEBAR -> "${height.terrainHeightTransformation(Format.SHORT3)}, ${type.terrainTypeTransformation(Format.SIDEBAR)}"
        Format.FULL    -> "Terrain height: ${height.terrainHeightTransformation(format)}, type: ${type.terrainTypeTransformation(format)}"
    }
}

internal val terrainTypeTransformation: Transformation<TerrainType> = { format ->
    when(format) {
        Format.ICON    -> when (this) {
            TerrainType.GRASSLAND     -> "G"
            TerrainType.FOREST        -> "F"
            TerrainType.MOUNTAIN      -> "M"
            TerrainType.HILL          -> "H"
            TerrainType.WATER_DEEP    -> "D"
            TerrainType.WATER_SHALLOW -> "S"
        }
        Format.SHORT3  -> when (this) {
            TerrainType.GRASSLAND     -> "Grs"
            TerrainType.FOREST        -> "Frs"
            TerrainType.MOUNTAIN      -> "Mnt"
            TerrainType.HILL          -> "Hil"
            TerrainType.WATER_DEEP    -> "Wdp"
            TerrainType.WATER_SHALLOW -> "Wsh"
        }
        Format.SHORT5  -> when (this) {
            TerrainType.GRASSLAND     -> "Grass"
            TerrainType.FOREST        -> "Fores"
            TerrainType.MOUNTAIN      -> "Mount"
            TerrainType.HILL          -> "Hills"
            TerrainType.WATER_DEEP    -> "WDeep"
            TerrainType.WATER_SHALLOW -> "WShlw"
        }
        Format.SIDEBAR, Format.FULL    -> when (this) {
            TerrainType.GRASSLAND,
            TerrainType.FOREST,
            TerrainType.MOUNTAIN      -> name.toLowerCase().capitalize()
            TerrainType.HILL          -> "Hills"
            TerrainType.WATER_DEEP    -> "Deep water"
            TerrainType.WATER_SHALLOW -> "Shallow water"
        }
    }
}

internal val terrainHeightTransformation: Transformation<TerrainHeight> = { format ->
    when(format) {
        Format.ICON    -> when {
            value < 1 -> Symbols.ARROW_UP.toString()
            value > 9 -> Symbols.ARROW_DOWN.toString()
            else      -> "$value"
        }
        Format.SHORT3  -> "$value"
        Format.SHORT5  -> "H: $value"
        Format.SIDEBAR -> "Height: $value"
        Format.FULL    -> "Terrain height: $value"
    }
}