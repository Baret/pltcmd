package de.gleex.pltcmd.model.radio

/*
class RadioSenderTest : StringSpec() {
    private val map = WorldMap.create(setOf(sectorAtWithTerrain(Coordinate(0, 0)) {
        Terrain.of(TerrainType.FOREST, TerrainHeight.FIVE)
    }))

    init {
        val coordinate = Coordinate(1, 2)
        val location = coordinate.toProperty()

        "reachableTiles of a minimal sender must be one tile" {
            val expectedReachableTilesMinimum = CoordinateRectangle(coordinate, 1, 1)

            val minimumSender = RadioSender(location, RadioSignal.MIN_POWER_THRESHOLD, map)
            minimumSender.reachableTiles shouldBe expectedReachableTilesMinimum

            val lessThenMinimumSender = RadioSender(location, 1.23, map)
            lessThenMinimumSender.reachableTiles shouldBe expectedReachableTilesMinimum
        }

        "reachableTiles of a powerful sender must be the full map" {
            val allMapCoordinates = map.sectors.flatMap { it.tiles.map(WorldTile::coordinate) }

            val powerfulSender = RadioSender(location, 23.45, map)
            powerfulSender.reachableTiles.toList() shouldBe allMapCoordinates
        }

        "reachableTiles of a normal sender must have full range in air" {
            // power 12.5 has 22 tiles reach over air
            val expectedReachableTiles = CoordinateRectangle(Coordinate(0,0), Coordinate(23,24))

            val normalSender = RadioSender(location, 12.5, map)
            normalSender.reachableTiles shouldBe expectedReachableTiles
        }
    }

}*/