package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.model.world.WorldTile
import java.util.*

/** All tiles that are currently visible for an entity. */
internal class VisibleAreaAttribute(val tiles: SortedSet<WorldTile>)
