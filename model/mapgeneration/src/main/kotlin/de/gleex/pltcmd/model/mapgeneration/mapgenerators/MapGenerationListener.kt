package de.gleex.pltcmd.model.mapgeneration.mapgenerators

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType

/** Is notified during map generation. */
interface MapGenerationListener {

    /** A new map will be generated at the given origin */
    fun startGeneration(origin: Coordinate)

    /** Fired if the tile of a map changed. */
    fun terrainGenerated(coordinate: Coordinate, terrainHeight: TerrainHeight?, terrainType: TerrainType?)

}