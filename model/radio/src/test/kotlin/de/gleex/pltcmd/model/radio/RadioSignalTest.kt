package de.gleex.pltcmd.model.radio

import de.gleex.pltcmd.model.radio.RadioSignal.Companion.AIR_LOSS_FACTOR
import de.gleex.pltcmd.model.radio.RadioSignal.Companion.GROUND_LOSS_FACTOR
import de.gleex.pltcmd.model.radio.RadioSignal.Companion.MIN_POWER_THRESHOLD
import de.gleex.pltcmd.model.radio.testhelpers.shouldBeExactly
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import io.kotlintest.matchers.doubles.plusOrMinus
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import kotlin.math.pow

class RadioSignalTest: WordSpec() {
    init
    {
        val testTerrain = Terrain.of(TerrainType.GRASSLAND, TerrainHeight.SEVEN)
        val rs = RadioSignal(100.0)
        "A $rs" should {
            "have max range 125" {
                rs.maxRange shouldBe 125
                val powerAfterMaxRangeInAir = 100.0 * AIR_LOSS_FACTOR.pow(rs.maxRange)
                powerAfterMaxRangeInAir shouldBe MIN_POWER_THRESHOLD.plus(0.0031).plusOrMinus(0.0031)
            }
            "have min range 7" {
                rs.minRange shouldBe 7
                val powerAfterMinRangeInGround = 100.0 * GROUND_LOSS_FACTOR.pow(rs.minRange)
                powerAfterMinRangeInGround shouldBe MIN_POWER_THRESHOLD.plus(0.2355).plusOrMinus(0.2355)
            }
        }
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
                    "lose power with the base loss factor of $AIR_LOSS_FACTOR" {
                        rs.along(lowerTiles).strength shouldBe AIR_LOSS_FACTOR.pow(i).plusOrMinus(0.001)
                    }
                }

                val higherTiles = listOf (testTerrain) + i.highestTerrainTiles()
                "travelling through ${lowerTiles.size} tiles of ground" should {
                    "lose power with the ground loss factor of $GROUND_LOSS_FACTOR" {
                        rs.along(higherTiles).strength shouldBe GROUND_LOSS_FACTOR.pow(i).plusOrMinus(0.001)
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