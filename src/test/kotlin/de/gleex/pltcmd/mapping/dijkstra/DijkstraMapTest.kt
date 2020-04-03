package de.gleex.pltcmd.mapping.dijkstra

import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.testhelpers.beEmpty
import de.gleex.pltcmd.testhelpers.shouldContainValue
import io.kotlintest.assertSoftly
import io.kotlintest.matchers.collections.shouldContainInOrder
import io.kotlintest.matchers.collections.shouldHaveSingleElement
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.should
import io.kotlintest.specs.WordSpec

class DijkstraMapTest: WordSpec({
    "A map with only a target" should {
        val target = Coordinate(2, 3)
        val map = DijkstraMap<Coordinate>(target)
        "should have a distance of 0 from $target" {
            map.distanceFrom(target) shouldContainValue 0
        }

        "should have another distance of 0 when adding another target" {
            val secondTarget = Coordinate(12, 34)
            map.addTarget(secondTarget)
            map.distanceFrom(secondTarget) shouldContainValue 0
        }

        "should not calculate any paths except from the target" {
            map.pathFrom(Coordinate(0, 0)) should beEmpty()
            val path = map.pathFrom(target)
            path.get() shouldHaveSingleElement target

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
            map.pathFrom(Coordinate(1,1)) should beEmpty()
        }

        "have a correct full path" {
            val path = map.pathFrom(c3).get()
            path shouldContainInOrder listOf(c3, c2, c1, target)
        }

        "have a path of 2 nodes when starting at the second" {
            assertSoftly {
                val path = map.pathFrom(c1).get()
                path shouldHaveSize 2
                path shouldContainInOrder listOf(c1, target)
            }
        }
    }
})
