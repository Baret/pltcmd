package de.gleex.pltcmd.game.engine.attributes.memory

import de.gleex.pltcmd.model.world.WorldArea
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import de.gleex.pltcmd.util.knowledge.Known

/**
 * Knowledge about the [WorldArea] defining the whole [WorldMap]. It is initialized as completely unrevealed
 * and get revealed over time.
 */
class KnownWorld(world: WorldMap) : Known<WorldArea, KnownWorld> {

    override val origin: WorldArea = world.asWorldArea()

    /**
     * All not yet revealed (aka. unknown) [Coordinate]s.
     */
    private val unrevealed: MutableList<Coordinate> =
        origin
            .tiles
            .map { it.coordinate }
            .toMutableList()

    /**
     * @return the [KnownTerrain] at the given location.
     */
    operator fun get(coordinate: Coordinate): KnownTerrain {
        val originalTerrain = origin[coordinate]
            .orElseGet { WorldTile(coordinate.eastingFromLeft, coordinate.northingFromBottom) }
        return originalTerrain.toKnownTerrain(coordinate.isRevealed())
    }

    /**
     * @return true if this [Coordinate] is contained in [unrevealed]
     */
    private fun Coordinate.isRevealed(): Boolean =
        indexOf(this) < 0

    private fun indexOf(coordinate: Coordinate): Int =
        unrevealed.binarySearch(coordinate)

    /**
     * Reveals the given [Coordinate].
     */
    infix fun reveal(toReveal: Coordinate) {
        val index = indexOf(toReveal)
        if (index >= 0) {
            unrevealed.removeAt(index)
        }
    }

    /**
     * Reveals the complete [WorldArea].
     */
    infix fun reveal(areaToReveal: CoordinateArea) {
        unrevealed.removeAll(areaToReveal)
    }

    override fun mergeWith(other: KnownWorld): KnownWorld =
        also {
            unrevealed
                .removeAll { it !in other.unrevealed }
        }

    /**
     * Another known world is richer than this one when it has a larger revealed area.
     */
    override infix fun isRicherThan(other: KnownWorld): Boolean {
        val myUnrevealedArea = CoordinateArea(unrevealed.toSortedSet())
        val otherUnrevealedArea = CoordinateArea(other.unrevealed.toSortedSet())
        return otherUnrevealedArea covers myUnrevealedArea
    }

    /**
     * Gets all unknown tiles in the given [CoordinateArea].
     *
     * All returned [KnownTerrain] are not [KnownTerrain.revealed].
     */
    fun getUnknownIn(area: CoordinateArea): CoordinateArea =
        CoordinateArea { (area intersect unrevealed).toSortedSet() }

    companion object

}