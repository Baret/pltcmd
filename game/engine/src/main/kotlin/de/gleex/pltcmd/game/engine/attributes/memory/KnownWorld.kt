package de.gleex.pltcmd.game.engine.attributes.memory

import de.gleex.pltcmd.model.world.WorldArea
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import de.gleex.pltcmd.util.knowledge.Known
import org.hexworks.cobalt.logging.api.LoggerFactory

/**
 * Knowledge about the [WorldArea] defining the whole [WorldMap]. It is initialized as completely unrevealed
 * and get revealed over time.
 */
class KnownWorld(world: WorldMap): Known<WorldArea, KnownWorld> {

    companion object {
        private val log = LoggerFactory.getLogger(KnownWorld::class)
    }

    init {
        log.debug("Getting world as worldArea")
    }

    override val origin: WorldArea = world.asWorldArea()

    init {
        log.debug("Got origin. Mapping to unrevealed...")
    }

    /**
     * All not yet revealed (aka. unknown) [Coordinate]s.
     */
    private val unrevealed: MutableList<Coordinate> =
        origin
            .tiles
            .map { it.coordinate }
            .toMutableList()

    init {
        log.debug("Finished creating ${unrevealed.size} unrevealed tiles.")
    }

    /**
     * @return the [KnownTerrain] at the given location.
     */
    operator fun get(coordinate: Coordinate): KnownTerrain {
        val originalTerrain = origin[coordinate]
            .orElseGet { WorldTile(coordinate.eastingFromLeft, coordinate.northingFromBottom) }
        return if (coordinate.isUnrevealed()) {
            originalTerrain.unknown()
        } else {
            KnownTerrain(
                origin = originalTerrain,
                isRevealed = true
            )
        }
    }

    /**
     * @return true if this [Coordinate] is contained in [unrevealed]
     */
    private fun Coordinate.isUnrevealed(): Boolean =
        indexOf(this) >= 0

    private fun indexOf(coordinate: Coordinate): Int =
        unrevealed.binarySearch(coordinate)

    /**
     * Reveals the given [Coordinate].
     */
    fun reveal(toReveal: Coordinate) {
        unrevealed.removeAt(indexOf(toReveal))
    }

    /**
     * Reveals the complete [WorldArea].
     */
    fun reveal(areaToReveal: WorldArea) {
        areaToReveal
            .tiles
            .forEach { reveal(it.coordinate) }
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

}