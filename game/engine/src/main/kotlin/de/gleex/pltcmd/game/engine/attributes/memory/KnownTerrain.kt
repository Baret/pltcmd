package de.gleex.pltcmd.game.engine.attributes.memory

import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.util.knowledge.Revealable

/**
 * Represents knowledge about a specific [WorldTile], or more exact: The terrain at its [Coordinate].
 *
 * Known terrain is either revealed or not. By default it is unrevealed (aka. unknown). [reveal] changes the
 * status. Unknown terrain has null [Terrain].
 *
 * **Hint:** Use [WorldTile.unrevealed] and [WorldTile.revealed] extension functions to create instances.
 */
class KnownTerrain internal constructor(knownTile: WorldTile, isRevealed: Boolean = false) : Revealable<WorldTile, KnownTerrain>(knownTile, isRevealed) {

    /** The currently known [Terrain]. `null` if not revealed. */
    val terrain: Terrain?
        get() = bit?.terrain

    /** The [Coordinate] of this [KnownTerrain]. It is always "the truth" independent of the revealed state. */
    val coordinate: Coordinate
        get() = origin.coordinate

    override fun copy(): KnownTerrain {
        return KnownTerrain(origin, revealed)
    }
}

/**
 * Creates a revealed [KnownTerrain].
 */
fun WorldTile.revealed() = KnownTerrain(this).apply { reveal() }

/**
 * Creates an unrevealed [KnownTerrain].
 */
fun WorldTile.unrevealed() = KnownTerrain(this)