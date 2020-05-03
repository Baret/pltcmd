package de.gleex.pltcmd.model.radio

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.testhelpers.randomSectorAt
import io.kotlintest.TestCase
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class RadioSenderTest : StringSpec() {
    private val map = WorldMap.create(setOf(randomSectorAt(Coordinate(0, 0))))
    lateinit var underTest: RadioSender

    private val expectedReachableTiles = listOf<Coordinate>()

    override fun beforeTest(testCase: TestCase) {
        underTest = RadioSender(CallSign("Testy"), Coordinate(1, 2), 123.45, map)
    }

    init {
        "reachableTiles" {
            underTest.reachableTiles shouldBe expectedReachableTiles
        }
    }

}