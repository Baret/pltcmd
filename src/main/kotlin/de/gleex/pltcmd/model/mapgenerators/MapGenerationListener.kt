package de.gleex.pltcmd.model.mapgenerators

import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import de.gleex.pltcmd.model.world.Coordinate

/** Is notified during map generation. */
interface MapGenerationListener {

    /** Fired if the tile of a map changed. */
    fun terrainGenerated(coordinate: Coordinate, terrainHeight: TerrainHeight?, terrainType: TerrainType?)

}