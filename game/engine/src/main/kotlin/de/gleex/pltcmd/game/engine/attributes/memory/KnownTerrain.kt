package de.gleex.pltcmd.game.engine.attributes.memory

import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.util.knowledge.KnownByBoolean

/**
 * Represents knowledge about a specific [WorldTile], or more exact: The terrain at its [Coordinate].
 *
 * Known terrain is either revealed or not. By default it is unrevealed (aka. unknown). [KnownByBoolean.reveal] changes the
 * status. Unknown terrain has null [Terrain].
 *
 * @see KnownByBoolean
 */
typealias KnownTerrain = KnownByBoolean<WorldTile, *>

/** The currently known [Terrain]. `null` if not revealed. */
val KnownTerrain.terrain: Terrain?
    get() = bit?.terrain

/** The [Coordinate] of this [KnownTerrain]. It is always "the truth" independent of the revealed state. */
val KnownTerrain.coordinate: Coordinate
    get() = origin.coordinate
