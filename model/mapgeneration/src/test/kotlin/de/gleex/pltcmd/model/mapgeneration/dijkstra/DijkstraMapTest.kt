package de.gleex.pltcmd.model.mapgeneration.dijkstra

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.util.graph.DijkstraMap
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe

/** Tests [DijkstraMap] with [Coordinate]s */
class DijkstraMapTest: WordSpec({
    "A map with only a target" should {
        val target = Coordinate(2, 3)
        val map = DijkstraMap<Coordinate>(target)
        "should have a distance of 0 from $target" {
            map.distanceFrom(target) shouldBe 0
        }

        "should have another distance of 0 when adding another target" {
            val secondTarget = Coordinate(12, 34)
            map.addTarget(secondTarget)
            map.distanceFrom(secondTarget) shouldBe 0
        }

        "should not calculate any paths except from the target" {
            map.pathFrom(Coordinate(0, 0)) should beEmpty()
            val path = map.pathFrom(target)
            path shouldHaveSingleElement target

        }
    }

    "A map with 3 nodes" should {
        val target = Coordinate(0, 0)
        val map = DijkstraMap(target)
        val c1 = Coordinate(1, 0)
        val c2 = Coordinate(2, 0)
        val c3 = Coordinate(2, 1)

        map.add(c1, target, 0)
        map.add(c2, c1, 1)
        map.add(c3, c2, 2)

        "have no path from a coordinate outside" {
            map.pathFrom(Coordinate(1,1)) should beNull()
        }

        "have a correct full path" {
            val path = map.pathFrom(c3)
            path shouldContainInOrder listOf(c3, c2, c1, target)
        }

        "have a path of 2 nodes when starting at the second" {
            assertSoftly {
                val path = map.pathFrom(c1)
                path shouldHaveSize 2
                path shouldContainInOrder listOf(c1, target)
            }
        }
    }
})
