package de.gleex.pltcmd.game.engine.attributes.memory

import de.gleex.pltcmd.model.world.WorldArea
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import de.gleex.pltcmd.model.world.coordinate.FilteredCoordinateArea
import de.gleex.pltcmd.util.knowledge.Known

/**
 * Knowledge about the [WorldMap]. It is initialized as completely unrevealed
 * and gets revealed over time.
 */
class KnownWorld(world: WorldMap) : Known<WorldMap, KnownWorld> {

    override val origin: WorldMap = world

    /**
     * The revealed area. It is a growing view onto [origin].
     */
    private val revealed: MutableSet<Coordinate> = mutableSetOf()

    /**
     * @return the [KnownTerrain] at the given location.
     */
    operator fun get(coordinate: Coordinate): KnownTerrain {
        val originalTile = origin[coordinate]
        return when {
            coordinate.isRevealed() -> originalTile.revealed()
            else                    -> originalTile.unrevealed()
        }
    }

    /**
     * @return true if this [Coordinate] is not contained in [unrevealed]
     */
    private fun Coordinate.isRevealed(): Boolean = this in revealed

    /**
     * Reveals the given [Coordinate].
     */
    infix fun reveal(toReveal: Coordinate) {
        reveal(CoordinateArea(toReveal))
    }

    /**
     * Reveals the complete [WorldArea].
     */
    infix fun reveal(areaToReveal: CoordinateArea) {
        revealed.addAll(areaToReveal)
    }

    override fun mergeWith(other: KnownWorld): Boolean {
        val knowPreviously = revealed.size
        revealed.addAll(other.revealed)
        return knowPreviously != revealed.size
    }

    override fun copy(): KnownWorld {
        return KnownWorld(origin)
    }

    /**
     * Gets all unknown tiles in the given [CoordinateArea].
     *
     * All returned [Coordinate]s are not revealed.
     */
    fun getUnknownIn(area: CoordinateArea): CoordinateArea =
        FilteredCoordinateArea(area) { it !in revealed }

}