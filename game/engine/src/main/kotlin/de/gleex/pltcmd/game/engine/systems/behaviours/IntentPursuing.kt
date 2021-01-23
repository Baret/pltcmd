package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.CommandersIntent
import de.gleex.pltcmd.game.engine.entities.types.asElementEntity
import de.gleex.pltcmd.game.engine.entities.types.commandersIntent
import de.gleex.pltcmd.game.engine.extensions.AnyGameEntity
import kotlinx.coroutines.runBlocking
import org.hexworks.amethyst.api.base.BaseBehavior

/**
 * This behavior simply [CommandersIntent.proceed]s the commander's intent.
 */
object IntentPursuing : BaseBehavior<GameContext>(CommandersIntent::class) {
    override suspend fun update(entity: AnyGameEntity, context: GameContext): Boolean {
        return entity.asElementEntity { element ->
            val messageToExecute = element.commandersIntent.proceed(element, context)
            if (messageToExecute.isPresent) {
                // FIXME: Use surrounding coroutine context (something like entity.receiveWhenElement{ ... }?)
                runBlocking {
                    element.receiveMessage(messageToExecute.get())
                }
            }
            messageToExecute.isPresent
        }.orElseGet {
            false
        }
    }
}
