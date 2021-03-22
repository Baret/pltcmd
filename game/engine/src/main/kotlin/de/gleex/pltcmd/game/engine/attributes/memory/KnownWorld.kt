package de.gleex.pltcmd.game.engine.attributes.memory

import de.gleex.pltcmd.model.world.Sector
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
class KnownWorld(world: WorldMap): Known<WorldArea, KnownWorld> {
    override val origin: WorldArea = world.asWorldArea()

    private val unrevealed: MutableMap<Coordinate, KnownTerrain> =
        origin
            .tiles
            .map { it.unknown() }
            .associateBy { it.coordinate }
            .toMutableMap()

    /**
     * @return the [KnownTerrain] at the given location.
     */
    operator fun get(coordinate: Coordinate): KnownTerrain {
        val unrevealedTerrain = unrevealed[coordinate]
        if (unrevealedTerrain != null) {
            return unrevealedTerrain
        }

        val originalTerrain = origin[coordinate]
        if (originalTerrain.isPresent) {
            return KnownTerrain(originalTerrain.get(), true)
        }
        return WorldTile(coordinate.eastingFromLeft, coordinate.northingFromBottom).unknown()
    }

    /**
     * Reveals the given [Coordinate].
     */
    fun reveal(toReveal: Coordinate) {
        unrevealed.remove(toReveal)
    }

    /**
     * Reveals the complete sector.
     */
    fun reveal(sectorToReveal: Sector) {
        sectorToReveal
            .tiles
            .map { it.coordinate }
            .forEach { reveal(it) }
    }

    override fun mergeWith(other: KnownWorld): KnownWorld =
        also {
            other
                .unrevealed
                .filterValues { it.revealed }
                .forEach { unrevealed.remove(it.key) }
        }

    /**
     * Another known world is richer than this one when it has a larger revealed area.
     */
    override infix fun isRicherThan(other: KnownWorld): Boolean {
        val myUnrevealedArea = CoordinateArea(unrevealed.keys.toSortedSet())
        val otherUnrevealedArea = CoordinateArea(other.unrevealed.keys.toSortedSet())
        return otherUnrevealedArea covers myUnrevealedArea
    }

}