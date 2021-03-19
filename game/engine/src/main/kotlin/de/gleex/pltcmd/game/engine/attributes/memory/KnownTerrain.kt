package de.gleex.pltcmd.game.engine.attributes.memory

import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import de.gleex.pltcmd.util.knowledge.Known
import de.gleex.pltcmd.util.knowledge.KnownByBoolean

/**
 * Represents knowledge about a specific [WorldTile], or more exact: The terrain at its [Coordinate]
 */
class KnownTerrain(
    override val origin: WorldTile,
    isRevealed: Boolean = false
): KnownByBoolean<WorldTile>(isRevealed) {

    val coordinate: Coordinate = origin.coordinate

    val height: TerrainHeight?
        get() = if (revealed) {
            origin.terrain.height
        } else {
            null
        }

    val type: TerrainType?
        get() = if (revealed) {
            origin.terrain.type
        } else {
            null
        }

    override fun mergeWith(other: Known<WorldTile>): Known<WorldTile> =
        also {
            if(other is KnownByBoolean) {
                if(other.revealed) {
                    reveal()
                }
            }
        }
}

/**
 * Wraps this [WorldTile] into a [KnownTerrain] that is not revealed.
 */
fun WorldTile.unknown() = KnownTerrain(
    origin = this,
    isRevealed = false
)