package de.gleex.pltcmd.game.engine.systems.facets

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.CommandersIntent
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.ElementType
import de.gleex.pltcmd.game.engine.entities.types.commandersIntent
import kotlinx.coroutines.runBlocking
import org.hexworks.amethyst.api.base.BaseBehavior
import org.hexworks.amethyst.api.entity.Entity
import org.hexworks.amethyst.api.entity.EntityType

object IntentPursuing : BaseBehavior<GameContext>(CommandersIntent::class) {
    @Suppress("UNCHECKED_CAST")
    override suspend fun update(entity: Entity<EntityType, GameContext>, context: GameContext): Boolean {
        return if (entity.type == ElementType) {
            val element = entity as ElementEntity
            if (element.commandersIntent.isFinished(element)) {
                false
            } else {
                element.commandersIntent.proceed(element, context)
                        .ifPresent {
                            runBlocking {
                                element.executeCommand(it)
                            }
                        }
                true
            }
        } else {
            false
        }

    }
}
