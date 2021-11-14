package de.gleex.pltcmd.model.world.graph

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import mu.KotlinLogging
import org.jgrapht.Graph
import org.jgrapht.GraphTests

private val log = KotlinLogging.logger {  }

class CoordinateGraphTest : WordSpec({
    "The coordinate graph" should {
        "automatically connect new Vertices if possible" {
            val g = CoordinateGraph<CoordinateVertex>()
            g.addWithLog(50, 50)
            g should beConnected()

            for(n in 52..60) {
                g.addWithLog(50, n)
                g shouldNot beConnected()
            }

            g.addWithLog(50, 51)
            g should beConnected()
        }
    }
})

fun <T : Graph<*, *>> beConnected(): Matcher<T> {
    return Matcher {
        MatcherResult(
            GraphTests.isConnected(it),
            "Graph should be connected",
            "Graph should not be connected"
        )
    }
}

private fun CoordinateGraph<CoordinateVertex>.addWithLog(easting: Int, northing: Int) {
    log.debug { "Adding ${Coordinate(easting, northing)}" }
    addVertex(v(easting, northing))
}

private fun v(easting: Int, northing: Int) = CoordinateVertex(Coordinate(easting, northing))