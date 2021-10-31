package de.gleex.pltcmd.game.engine.attributes.memory

import de.gleex.pltcmd.model.world.WorldArea
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import de.gleex.pltcmd.util.knowledge.Known
import java.util.*

/**
 * Knowledge about the [WorldArea] defining the whole [WorldMap]. It is initialized as completely unrevealed
 * and get revealed over time.
 */
class KnownWorld(override val origin: WorldArea) : Known<WorldArea, KnownWorld> {

    constructor(world: WorldMap) : this(world.asWorldArea())

    /**
     * All not yet revealed (aka. unknown) [Coordinate]s.
     */
    private val unrevealed: SortedSet<Coordinate> =
        origin
        // create a local copy
        .toSortedSet()

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
    private fun Coordinate.isRevealed(): Boolean =
        unrevealed.contains(this).not()

    /**
     * Reveals the given [Coordinate].
     */
    infix fun reveal(toReveal: Coordinate) {
        unrevealed.remove(toReveal)
    }

    /**
     * Reveals the complete [WorldArea].
     */
    infix fun reveal(areaToReveal: CoordinateArea) {
        unrevealed.removeAll(areaToReveal)
    }

    override fun mergeWith(other: KnownWorld): Boolean {
        return unrevealed
            .removeAll { it !in other.unrevealed }
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
        area intersect CoordinateArea(this::unrevealed)

    companion object

}