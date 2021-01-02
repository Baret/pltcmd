package de.gleex.pltcmd.game.engine.systems.facets

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.entities.types.currentPosition
import de.gleex.pltcmd.game.engine.messages.UpdatePosition
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.Response
import org.hexworks.amethyst.api.base.BaseFacet

/**
 * Updates the [PositionAttribute] upon [UpdatePosition] messages.
 */
object PositionChanging : BaseFacet<GameContext, UpdatePosition>(UpdatePosition::class, PositionAttribute::class) {
    override suspend fun receive(message: UpdatePosition): Response {
        val (_, newPosition, _, entity) = message
        entity.currentPosition = newPosition
        return Consumed
    }
}
