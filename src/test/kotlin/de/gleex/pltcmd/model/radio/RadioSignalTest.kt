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
    private class RadioSignalTestExtension : RadioSignal(200.0) {
        fun convertedToPercent(testValue: Double) = testValue.toPercent()
    }

    init
    {
        "Remaining signal strength converted to percent" should {
            "be 1.0 when > 100 and 0.0 when < ${RadioSignal.MIN_STRENGTH_THRESHOLD}" {
                val radioSignal = RadioSignalTestExtension()
                forall(
                        // Rounding precision is 5 digits
                        row(Double.MAX_VALUE, 1.0),
                        row(110.0, 1.0),
                        row(100.0, 1.0),
                        row(99.8997, 0.999),
                        row(90.0, 0.90),
                        row(80.0, 0.80),
                        row(71.987654321, 0.71988),
                        row(70.0, 0.70),
                        row(60.0, 0.60),
                        row(50.0, 0.50),
                        row(40.0, 0.40),
                        row(30.0, 0.30),
                        row(20.0, 0.20),
                        row(10.0, 0.10),
                        row(10.000000001, 0.100000000),
                        row(1.000000001, 0.0),
                        row(0.0, 0.0),
                        row(-0.01, 0.0),
                        row(-0.0000000000000000000004, 0.0),
                        row(Double.MIN_VALUE, 0.0)
                ) { signalStrength, expectedPercent ->
                    radioSignal.convertedToPercent(signalStrength) shouldBeExactly expectedPercent
                }
            }
        }

        val testTerrain = Terrain.of(TerrainType.GRASSLAND, TerrainHeight.SEVEN)
        val rs = RadioSignal(100.0)
        "A $rs" When {
            "getting an empty list to travel along" should {
                "be full strength" {
                    rs.along(listOf()) shouldBeExactly 1.0
                }
            }

            "getting a single field to travel along" should {
                "be full strength because it is the field it is originating from" {
                    rs.along(1.highestTerrainTiles()) shouldBeExactly 1.0
                    rs.along(1.lowestTerrainTiles()) shouldBeExactly 1.0
                    rs.along(listOf(testTerrain)) shouldBeExactly 1.0
                }
            }

            for (i in 1..6) {
                val lowerTiles = listOf (testTerrain) + i.lowestTerrainTiles()
                "travelling through ${lowerTiles.size} tiles of air" should {
                    val airLossFactor = RadioSignal.BASE_LOSS_FACTOR
                    "lose strength with the base loss factor of $airLossFactor" {
                        rs.along(lowerTiles).shouldBe(airLossFactor.pow(i).plusOrMinus(0.001))
                    }
                }

                val higherTiles = listOf (testTerrain) + i.highestTerrainTiles()
                "travelling through ${lowerTiles.size} tiles of ground" should {
                    val groundLossFactor = RadioSignal.TERRAIN_LOSS_FACTOR
                    "lose strength with the ground loss factor of $groundLossFactor" {
                        rs.along(higherTiles).shouldBe(groundLossFactor.pow(i).plusOrMinus(0.001))
                    }
                }
            }
        }
    }
}

/**
 * Creates a list of n "tiles" of lowest [TerrainHeight].
 */
private fun Int.lowestTerrainTiles() =
        List(this) { _ -> Terrain.of(TerrainType.WATER_DEEP, TerrainHeight.MIN)}

/**
 * Creates a list of n "tiles" of highest [TerrainHeight].
 */
private fun Int.highestTerrainTiles() =
        List(this) { _ -> Terrain.of(TerrainType.MOUNTAIN, TerrainHeight.MAX)}