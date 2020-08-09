package de.gleex.pltcmd.game.engine.systems.facets

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.CommandersIntent
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.ElementType
import de.gleex.pltcmd.game.engine.entities.types.commandersIntent
import de.gleex.pltcmd.game.engine.extensions.AnyGameEntity
import kotlinx.coroutines.runBlocking
import org.hexworks.amethyst.api.base.BaseBehavior

/**
 * This behavior simply [CommandersIntent.proceed]s the commander's intent.
 */
object IntentPursuing : BaseBehavior<GameContext>(CommandersIntent::class) {
    @Suppress("UNCHECKED_CAST")
    override suspend fun update(entity: AnyGameEntity, context: GameContext): Boolean {
        return if (entity.type == ElementType) {
            val element = entity as ElementEntity
            val commandToExecute = element.commandersIntent.proceed(element, context)
            commandToExecute
                    .ifPresent {
                        runBlocking {
                            element.executeCommand(it)
                        }
                    }
            commandToExecute.isPresent
        } else {
            false
        }

    }
}
