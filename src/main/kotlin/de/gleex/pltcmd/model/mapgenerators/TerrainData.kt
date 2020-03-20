package de.gleex.pltcmd.model.mapgenerators

import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType

/** Holds the mutable data for the final terrain. */
data class TerrainData(var height: TerrainHeight?, var type: TerrainType?)