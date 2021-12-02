package de.gleex.pltcmd.game.engine.attributes.memory

import de.gleex.pltcmd.model.world.WorldArea
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import de.gleex.pltcmd.util.knowledge.Known

/**
 * Knowledge about the [WorldArea] defining the whole [WorldMap]. It is initialized as completely unrevealed
 * and gets revealed over time.
 */
class KnownWorld(world: WorldMap) : Known<WorldArea, KnownWorld> {

    override val origin: WorldArea = world.area

    /**
     * The revealed area. It is a growing view onto [origin].
     */
    private var revealed: WorldArea = WorldArea.EMPTY

    /**
     * @return the [KnownTerrain] at the given location.
     */
    operator fun get(coordinate: Coordinate): KnownTerrain {
        val originalTerrain = origin[coordinate]
            .orElseGet { WorldTile(coordinate.eastingFromLeft, coordinate.northingFromBottom) }
        return when {
            coordinate.isRevealed() -> originalTerrain.revealed()
            else                    -> originalTerrain.unrevealed()
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
        revealed += origin.intersect(areaToReveal)
    }

    override fun mergeWith(other: KnownWorld): Boolean {
        val knowPreviously = revealed.size
        revealed += other.revealed
        return knowPreviously != revealed.size
    }

    /**
     * Gets all unknown tiles in the given [CoordinateArea].
     *
     * All returned [Coordinate]s are not revealed.
     */
    fun getUnknownIn(area: CoordinateArea): CoordinateArea =
        CoordinateArea { area.filter { it !in revealed }.toSortedSet() }

    companion object

}