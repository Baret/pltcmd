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
class KnownTerrain(
    override val origin: WorldTile
) : KnownByBoolean<WorldTile, KnownTerrain>(isRevealed = false) {

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

    override fun toString(): String {
        return "KnownTerrain(origin=$origin, height=$height, type=$type, revealed=$revealed)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as KnownTerrain

        if (origin != other.origin) return false
        if (height != other.height) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + origin.hashCode()
        result = 31 * result + (height?.hashCode() ?: 0)
        result = 31 * result + (type?.hashCode() ?: 0)
        return result
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

/**
 * Creates a [KnownTerrain] from this [WorldTile] that is either [revealed] or not.
 *
 * @param revealed when true, a [known] terrain will be created, [unknown] otherwise.
 */
fun WorldTile.toKnownTerrain(revealed: Boolean) =
    if (revealed) {
        this.known()
    } else {
        this.unknown()
    }