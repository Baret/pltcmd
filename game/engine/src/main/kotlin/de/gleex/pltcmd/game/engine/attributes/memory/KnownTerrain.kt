package de.gleex.pltcmd.game.engine.attributes.memory

import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import de.gleex.pltcmd.util.knowledge.KnownByBoolean

/**
 * Represents knowledge about a specific [WorldTile], or more exact: The terrain at its [Coordinate]
 */
class KnownTerrain(
    override val origin: WorldTile,
    isRevealed: Boolean = false
): KnownByBoolean<WorldTile, KnownTerrain>(isRevealed) {

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
 * Wraps this [WorldTile] into a [KnownTerrain] that is not revealed.
 */
fun WorldTile.unknown() = KnownTerrain(
    origin = this,
    isRevealed = false
)