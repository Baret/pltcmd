package de.gleex.pltcmd.model.radio

import de.gleex.pltcmd.model.terrain.Terrain
import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import io.kotlintest.data.forall
import io.kotlintest.matchers.doubles.plusOrMinus
import io.kotlintest.matchers.doubles.shouldBeExactly
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.kotlintest.tables.row
import kotlin.math.pow

class RadioSignalTest: WordSpec() {
    private class RadioSignalTestExtension : RadioSignal(200.0, Terrain(TerrainType.GRASSLAND, TerrainHeight.ONE)) {
        fun convertedToPercent(testValue: Double) = testValue.toPercent()
    }

    init {
        "Remaining signal strength converted to percent" should {
            "be 1.0 when > 100 and 0.0 when < 0" {
                val radioSignal = RadioSignalTestExtension()
                forall(
                        row(Double.MAX_VALUE, 1.0),
                        row(110.0, 1.0),
                        row(100.0, 1.0),
                        row(99.8997, 0.998997),
                        row(90.0, 0.90),
                        row(80.0, 0.80),
                        row(71.987654321, 0.71987654321),
                        row(70.0, 0.70),
                        row(60.0, 0.60),
                        row(50.0, 0.50),
                        row(40.0, 0.40),
                        row(30.0, 0.30),
                        row(20.0, 0.20),
                        row(10.0, 0.10),
                        row(1.000000001, 0.01000000001),
                        row(0.0, 0.0),
                        row(-0.01, 0.0),
                        row(-0.0000000000000000000004, 0.0),
                        row(Double.MIN_VALUE, 0.0)
                ) { signalStrength, expectedPercent ->
                    radioSignal.convertedToPercent(signalStrength) shouldBeExactly expectedPercent
                }
            }
        }

        val testTerrain = Terrain(TerrainType.GRASSLAND, TerrainHeight.SEVEN)
        val rs = RadioSignal(100.0, testTerrain)
        "A $rs" When {
            "getting an empty list to travel along" should {
                "be full strength" {
                    rs.along(listOf()) shouldBeExactly 1.0
                }
            }

            // TODO: activate this test (remove "!") and fix implementation of RadioSignal
            "!getting a single field to travel along" should {
                "be full strength because it is the field it is originating from" {
                    rs.along(1.highestTerrainTiles()) shouldBeExactly 1.0
                }
            }

            for (i in 1..6) {
                val lowerTiles = i.lowestTerrainTiles()
                "travelling through $i tiles of air" should {
                    val airLossFactor = 0.98
                    "lose strength with the base loss factor of $airLossFactor" {
                        rs.along(lowerTiles).shouldBe(airLossFactor.pow(i).plusOrMinus(0.001))
                    }
                }

                val higherTiles = i.highestTerrainTiles()
                "travelling through $i tiles of ground" should {
                    val groundLossFactor = 0.70
                    "lose strength with the ground loss factor of $groundLossFactor" {
                        rs.along(higherTiles).shouldBe(groundLossFactor.pow(i).toDouble().plusOrMinus(0.001))
                    }
                }
            }
        }
    }
}

/**
 * Creates a list of n "tiles" of [TerrainHeight] ONE.
 */
private fun Int.lowestTerrainTiles(): List<Terrain> {
    val terrainList = mutableListOf<Terrain>()
    repeat(this) {
        terrainList.add(Terrain(TerrainType.GRASSLAND, TerrainHeight.ONE))
    }
    return terrainList.toList()
}

/**
 * Creates a list of n "tiles" of [TerrainHeight] TEN.
 */
private fun Int.highestTerrainTiles(): List<Terrain> {
    val terrainList = mutableListOf<Terrain>()
    repeat(this) {
        terrainList.add(Terrain(TerrainType.GRASSLAND, TerrainHeight.TEN))
    }
    return terrainList.toList()
}