package de.gleex.pltcmd.model.radio

import de.gleex.pltcmd.model.terrain.Terrain
import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import io.kotlintest.data.forall
import io.kotlintest.matchers.doubles.shouldBeExactly
import io.kotlintest.specs.StringSpec
import io.kotlintest.tables.row
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals

class RadioSignalTest: StringSpec() {
    private class RadioSignalTestExtension : RadioSignal(200.0, Terrain(TerrainType.GRASSLAND, TerrainHeight.ONE)) {
        fun convertedToPercent(testValue: Double) = testValue.toPercent()
    }

    init {
        "Remaining signal strength converted to percent" {
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

    @Test
    fun `test base loss factor (signal travels through air)`() {
        val rs = RadioSignal(100.0, Terrain(TerrainType.GRASSLAND, TerrainHeight.FIVE))
        assertEquals(
                .98,
                rs.along(listOf(Terrain(TerrainType.GRASSLAND, TerrainHeight.ONE))))
        assertEquals(
                .98.pow(3),
                rs.along(listOf(Terrain(TerrainType.GRASSLAND, TerrainHeight.ONE),
                                Terrain(TerrainType.GRASSLAND, TerrainHeight.ONE),
                                Terrain(TerrainType.GRASSLAND, TerrainHeight.ONE))))
    }

    @Test
    fun `test terrain loss factor (signal travels through ground)`() {
        val rs = RadioSignal(100.0, Terrain(TerrainType.GRASSLAND, TerrainHeight.FIVE))
        assertEquals(0.70, rs.along(listOf(Terrain(TerrainType.GRASSLAND, TerrainHeight.TEN))))

        assertEquals(
                0.70.toBigDecimal().pow(3).toDouble(),
                rs.along(listOf(Terrain(TerrainType.GRASSLAND, TerrainHeight.TEN),
                        Terrain(TerrainType.GRASSLAND, TerrainHeight.SEVEN),
                        Terrain(TerrainType.GRASSLAND, TerrainHeight.SIX))))
    }
}
