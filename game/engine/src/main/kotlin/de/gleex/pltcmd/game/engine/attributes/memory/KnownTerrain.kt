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



    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KnownTerrain) return false

        if (origin != other.origin) return false
        if (coordinate != other.coordinate) return false
        if (height != other.height) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = origin.hashCode()
        result = 31 * result + coordinate.hashCode()
        result = 31 * result + (height?.hashCode() ?: 0)
        result = 31 * result + (type?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "KnownTerrain(origin=$origin, coordinate=$coordinate, height=$height, type=$type)"
    }
}

/**
 * Wraps this [WorldTile] into a [KnownTerrain] that is not revealed.
 */
fun WorldTile.unknown() = KnownTerrain(
    origin = this,
    isRevealed = false
)