package de.gleex.pltcmd.model.mapgenerators.data

import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.model.world.Sector
import io.kotlintest.data.suspend.forall
import io.kotlintest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotlintest.specs.WordSpec
import io.kotlintest.tables.row
import org.hexworks.cobalt.logging.api.LoggerFactory

class MutableWorldTest: WordSpec()
{
    private fun east(): Coordinate.() -> Coordinate = { this.withRelativeEasting(1) }
    private fun west(): Coordinate.() -> Coordinate = { this.withRelativeEasting(-1) }
    private fun north(): Coordinate.() -> Coordinate = { this.withRelativeNorthing(1) }
    private fun south(): Coordinate.() -> Coordinate = { this.withRelativeNorthing(-1) }
    private val allNeighbors = listOf(north(), east(), south(), west())
    private val westBorder = listOf(north(), east(), south())
    private val eastBorder = listOf(north(), west(), south())
    private val northBorder = listOf(west(), south(), east())
    private val southBorder = listOf(west(), north(), east())

    private val log = LoggerFactory.getLogger(this::class)

    init {
        val bottomLeftCoordinate = Coordinate(0, 0)
        val world = MutableWorld(bottomLeftCoordinate, Sector.TILE_COUNT, Sector.TILE_COUNT)
        "A world from ${world.bottomLeftCoordinate} to ${world.topRightCoordinate} of size ${world.worldSizeWidthInTiles} by ${world.worldSizeHeightInTiles} tiles" should {
            forall(
                    // SW corner
                    row(0, 0, listOf(east(), north())),
                    row(0, 1, westBorder),
                    row(1, 0, southBorder),
                    row(1, 1, allNeighbors),

                    // NE corner
                    row(49, 49, listOf(west(), south())),
                    row(49, 48, eastBorder),
                    row(48, 49, northBorder),
                    row(48, 48, allNeighbors),

                    // Some inside
                    row(2, 2, allNeighbors),
                    row(42, 13, allNeighbors),
                    row(49, 23, eastBorder),
                    row(9, 19, allNeighbors),

                    // some outside
                    row(-1, 0, emptyList()),
                    row(1, 50, emptyList()),
                    row(374, 983, emptyList()),
                    row(Int.MAX_VALUE, Int.MAX_VALUE, emptyList()),
                    row(Int.MIN_VALUE, Int.MIN_VALUE, emptyList()),
                    row(50, -1, emptyList())
            ) { easting, northing, neighborGenerators ->
                val c = Coordinate(easting, northing)
                val validNeighbors = neighborGenerators.map {
                    log.debug("Invoking on $c")
                    it.invoke(c)
                }
                "have ${validNeighbors.size} neighbors at $c" {
                    world.neighborsOf(c) shouldContainExactlyInAnyOrder validNeighbors
                }
            }
        }
    }
}
