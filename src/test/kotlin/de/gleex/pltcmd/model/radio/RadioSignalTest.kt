package de.gleex.pltcmd.model.radio

import de.gleex.pltcmd.model.terrain.Terrain
import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import org.junit.jupiter.api.Assertions
import kotlin.test.Test

class RadioSignalTest {
    @Test
    fun `strength of 100 should last 1 km`() {
        val flatTerrain = Terrain(TerrainType.GRASSLAND, TerrainHeight.ONE)
        val sig = RadioSignal(100.0, flatTerrain)
        val terrainList = mutableListOf<Terrain>()
        for(i in 1..10) { terrainList += flatTerrain.copy() }
        Assertions.assertEquals(10, terrainList.size)
        Assertions.assertEquals(20, sig.along(terrainList))
    }
}