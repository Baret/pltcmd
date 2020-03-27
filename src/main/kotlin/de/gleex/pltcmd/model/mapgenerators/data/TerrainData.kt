package de.gleex.pltcmd.model.mapgenerators.data

import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType

/** Holds the mutable data for the final terrain. */
data class TerrainData(var height: TerrainHeight? = null, var type: TerrainType? = null) {

    fun isComplete(): Boolean = height != null && type != null

    fun update(height: TerrainHeight?, type: TerrainType?) {
        this.height = height
        this.type = type
    }

}