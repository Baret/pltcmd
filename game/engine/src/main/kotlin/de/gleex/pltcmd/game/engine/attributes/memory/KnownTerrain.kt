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
typealias KnownTerrain = KnownByGrade<WorldTile, *>

/** The currently known [Terrain]. `null` if not revealed. */
val KnownTerrain.terrain: Maybe<Terrain>
    get() = revealAt(KnowledgeGrade.FULL) { it.terrain }

/** The [Coordinate] of this [KnownTerrain]. It is always "the truth" independent of the revealed state. */
val KnownTerrain.coordinate: Coordinate
    get() = origin.coordinate
