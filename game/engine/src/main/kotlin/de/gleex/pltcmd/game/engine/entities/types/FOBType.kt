package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.attributes.RadioAttribute
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import de.gleex.pltcmd.model.elements.CallSign
import org.hexworks.amethyst.api.base.BaseEntityType

/**
 * The entity type for Forward Operating Bases (FOBs).
 */
object FOBType : BaseEntityType("fob", "A stationary base."), Communicating, Seeing

/**
 * An entity of type [FOBType].
 */
typealias FOBEntity = GameEntity<FOBType>

/**
 * The [CallSign] of this base.
 */
val FOBEntity.callSign: CallSign
    get() = getAttribute(RadioAttribute::class).communicator.callSign