package de.gleex.pltcmd.model.radio

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import de.gleex.pltcmd.model.world.testhelpers.sectorAtWithTerrain
import io.kotlintest.TestCase
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class RadioSenderTest : StringSpec() {
    private val map = WorldMap.create(setOf(sectorAtWithTerrain(Coordinate(0, 0)) {
        Terrain.of(TerrainType.FOREST, TerrainHeight.FIVE)
    }))
    lateinit var underTest: RadioSender

    private val expectedReachableTiles = listOf<Coordinate>(
            Coordinate(0,3), Coordinate(1,3), Coordinate(2,3),
            Coordinate(0,2), Coordinate(1,2), Coordinate(2,2),
            Coordinate(0,1), Coordinate(1,1), Coordinate(2,1)
    )

    override fun beforeTest(testCase: TestCase) {
        underTest = RadioSender(CallSign("Testy"), Coordinate(1, 2), 123.45, map)
    }

    init {
        "reachableTiles must be the full map" {
            underTest.reachableTiles.toList() shouldBe map.sectors.flatMap { it.tiles.map { tile -> tile.coordinate } }.toList()
        }
    }

}