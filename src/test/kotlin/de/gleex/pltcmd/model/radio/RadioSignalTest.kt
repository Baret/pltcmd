package de.gleex.pltcmd.model.radio

import de.gleex.pltcmd.model.terrain.Terrain
import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals

class RadioSignalTest {
    private class RadioSignalTestExtension: RadioSignal(200.0, Terrain(TerrainType.GRASSLAND, TerrainHeight.ONE)) {
        fun testToPercent(testValue: Double) = testValue.toPercent()
    }

    @Test
    fun `test radio strength percentage`() {
        val rs = RadioSignalTestExtension()
        assertEquals(1.0, rs.testToPercent(Double.MAX_VALUE))
        assertEquals(1.0, rs.testToPercent(110.0))
        assertEquals(1.0, rs.testToPercent(100.0))
        assertEquals(0.998997, rs.testToPercent(99.8997))
        assertEquals(.90, rs.testToPercent(90.0))
        assertEquals(.80, rs.testToPercent(80.0))
        assertEquals(.71987654321, rs.testToPercent(71.987654321))
        assertEquals(.60, rs.testToPercent(60.0))
        assertEquals(.50, rs.testToPercent(50.0))
        assertEquals(.40, rs.testToPercent(40.0))
        assertEquals(.30, rs.testToPercent(30.0))
        assertEquals(.20, rs.testToPercent(20.0))
        assertEquals(.10, rs.testToPercent(10.0))
        assertEquals(.01000000001, rs.testToPercent(1.000000001))
        assertEquals(0.0, rs.testToPercent(0.0))
        assertEquals(0.0, rs.testToPercent(-0.01))
        assertEquals(0.0, rs.testToPercent(Double.MIN_VALUE))
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
