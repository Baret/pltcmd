package de.gleex.pltcmd.game.engine.attributes.memory

import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.util.knowledge.Knowledge
import org.hexworks.amethyst.api.base.BaseAttribute

/**
 * This attribute holds all the knowledge of an entity.
 */
class Memory: BaseAttribute() {
    val knownTerrain: Knowledge<WorldTile, KnownTerrain> = Knowledge()
}
