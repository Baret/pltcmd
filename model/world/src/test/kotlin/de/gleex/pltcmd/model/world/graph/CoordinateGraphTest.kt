package de.gleex.pltcmd.model.world.graph

import mu.KotlinLogging

private val log = KotlinLogging.logger {  }

//class CoordinateGraphTest : WordSpec({
//    "The coordinate graph" should {
//        "automatically connect new Vertices if possible" {
//            val g = CoordinateGraph<CoordinateVertex>()
//            g.addWithLog(50, 50)
//            g should beConnected()
//
//            for(n in 52..60) {
//                g.addWithLog(50, n)
//                g shouldNot beConnected()
//            }
//
//            g.addWithLog(50, 51)
//            g should beConnected()
//        }
//
//        "always have the correct min coordinate" {
//            val g = CoordinateGraph<CoordinateVertex>()
//            log.info { "min vs max: ${g.min.compareTo(Coordinate.maximum)}" }
//            g.min shouldBe Coordinate.maximum
//            g.addVertex(v(-9_999, -88_888))
//            val other = Coordinate(-9_999, -88_888)
//            log.info { "${g.min} vs $other: ${g.min.compareTo(other)} so < == ${g.min < other}" }
//            g.min shouldBe other
//            g.addVertex(v(10, 10))
//            g.min shouldBe other
//            g.addVertex(v(-10_000, -88_888))
//            g.min shouldBe Coordinate(-10_000, -88_888)
//        }
//
//        "always have the correct max coordinate" {
//            val g = CoordinateGraph<CoordinateVertex>()
//            log.info { "min vs max: ${g.min.compareTo(Coordinate.maximum)}" }
//            g.min shouldBe Coordinate.maximum
//            g.addVertex(v(-9_999, -88_888))
//            val other = Coordinate(-9_999, -88_888)
//            log.info { "${g.min} vs $other: ${g.min.compareTo(other)} so < == ${g.min < other}" }
//            g.min shouldBe other
//            g.addVertex(v(10, 10))
//            g.min shouldBe other
//            g.addVertex(v(-10_000, -88_888))
//            g.min shouldBe Coordinate(-10_000, -88_888)
//        }
//    }
//})
//
//fun <T : Graph<*, *>> beConnected(): Matcher<T> {
//    return Matcher {
//        MatcherResult(
//            GraphTests.isConnected(it),
//            "Graph should be connected",
//            "Graph should not be connected"
//        )
//    }
//}
//
//private fun CoordinateGraph<CoordinateVertex>.addWithLog(easting: Int, northing: Int) {
//    log.debug { "Adding ${Coordinate(easting, northing)}" }
//    addVertex(v(easting, northing))
//}
//
//private fun v(easting: Int, northing: Int) = CoordinateVertex(Coordinate(easting, northing))