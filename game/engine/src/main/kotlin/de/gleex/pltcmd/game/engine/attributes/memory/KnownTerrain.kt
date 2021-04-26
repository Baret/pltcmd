package de.gleex.pltcmd.game.engine.attributes.memory

import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import de.gleex.pltcmd.util.knowledge.KnownByBoolean

/**
 * Represents knowledge about a specific [WorldTile], or more exact: The terrain at its [Coordinate].
 *
 * Known terrain is either revealed or not. By default it is unrevealed (aka. unknown). [reveal] changes the
 * status. Unknown terrain has null [TerrainHeight] and [TerrainType].
 *
 * **Hint:** Use [WorldTile.unknown] and [WorldTile.known] extension functions to create instances.
 */
data class KnownTerrain(
    override val origin: WorldTile
): KnownByBoolean<WorldTile, KnownTerrain>(isRevealed = false) {

    /**
     * The [Coordinate] of this [KnownTerrain]. It is always "the truth".
     */
    val coordinate: Coordinate = origin.coordinate

    /**
     * The currently known [TerrainHeight].
     */
    val height: TerrainHeight?
        get() = if (revealed) {
            origin.terrain.height
        } else {
            null
        }

    /**
     * The currently know [TerrainType].
     */
    val type: TerrainType?
        get() = if (revealed) {
            origin.terrain.type
        } else {
            null
        }
}

/**
 * Creates a [KnownTerrain] from this [WorldTile] that is not revealed.
 */
fun WorldTile.unknown() = KnownTerrain(
    origin = this
)

/**
 * Creates an [KnownTerrain] from this [WorldTile] that is already revealed.
 */
fun WorldTile.known() =
    unknown()
        .also { it.reveal() }