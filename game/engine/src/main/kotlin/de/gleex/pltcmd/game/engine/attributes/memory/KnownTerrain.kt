package de.gleex.pltcmd.game.engine.attributes.memory

import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.util.knowledge.KnowledgeGrade
import de.gleex.pltcmd.util.knowledge.KnownByGrade
import org.hexworks.cobalt.datatypes.Maybe

/**
 * Represents knowledge about a specific [WorldTile], or more exact: The terrain at its [Coordinate].
 *
 * Known terrain is either revealed or not. By default it is unrevealed (aka. unknown). [reveal] changes the
 * status. Unknown terrain has null [Terrain].
 *
 * **Hint:** Use [WorldTile.unknown] and [WorldTile.known] extension functions to create instances.
 */
class KnownTerrain internal constructor(knownTile: WorldTile, grade: KnowledgeGrade): KnownByGrade<WorldTile, KnownTerrain>(knownTile, grade) {

    /** The currently known [Terrain]. `null` if not revealed. */
    val terrain: Maybe<Terrain>
        get() = revealAt(KnowledgeGrade.FULL) { it.terrain }

    /** The [Coordinate] of this [KnownTerrain]. It is always "the truth" independent of the revealed state. */
    val coordinate: Coordinate
        get() = origin.coordinate

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as KnownTerrain

        if (terrain != other.terrain) return false
        if (coordinate != other.coordinate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = terrain.hashCode()
        result = 31 * result + coordinate.hashCode()
        return result
    }

}

/**
 * Creates a revealed [KnownTerrain].
 */
fun WorldTile.known() = KnownTerrain(this, KnowledgeGrade.FULL)

/**
 * Creates an unrevealed [KnownTerrain].
 */
fun WorldTile.unknown() = KnownTerrain(this, KnowledgeGrade.NONE)