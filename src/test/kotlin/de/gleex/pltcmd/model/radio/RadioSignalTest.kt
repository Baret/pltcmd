package de.gleex.pltcmd.model.radio

import de.gleex.pltcmd.model.terrain.Terrain
import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import de.gleex.pltcmd.testhelpers.shouldBeExactly
import io.kotlintest.matchers.doubles.plusOrMinus
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import kotlin.math.pow

class RadioSignalTest: WordSpec() {
    init
    {
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
                    val airLossFactor = RadioSignal.AIR_LOSS_FACTOR
                    "lose power with the base loss factor of $airLossFactor" {
                        rs.along(lowerTiles).strength.shouldBe(airLossFactor.pow(i).plusOrMinus(0.001))
                    }
                }

                val higherTiles = listOf (testTerrain) + i.highestTerrainTiles()
                "travelling through ${lowerTiles.size} tiles of ground" should {
                    val groundLossFactor = RadioSignal.GROUND_LOSS_FACTOR
                    "lose power with the ground loss factor of $groundLossFactor" {
                        rs.along(higherTiles).strength.shouldBe(groundLossFactor.pow(i).plusOrMinus(0.001))
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
        List(this) { Terrain.of(TerrainType.WATER_DEEP, TerrainHeight.MIN)}

/**
 * Creates a list of n "tiles" of highest [TerrainHeight].
 */
private fun Int.highestTerrainTiles() =
        List(this) { Terrain.of(TerrainType.MOUNTAIN, TerrainHeight.MAX)}