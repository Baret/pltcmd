package de.gleex.pltcmd.game.engine.attributes.memory

import de.gleex.pltcmd.game.engine.attributes.memory.elements.KnownContact
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.util.knowledge.Knowledge
import org.hexworks.amethyst.api.base.BaseAttribute

/**
 * This attribute holds all the knowledge of an entity.
 */
class Memory(world: WorldMap): BaseAttribute() {
    val knownWorld: KnownWorld = KnownWorld(world)
    val knownContacts = Knowledge<ElementEntity, KnownContact>()
}
