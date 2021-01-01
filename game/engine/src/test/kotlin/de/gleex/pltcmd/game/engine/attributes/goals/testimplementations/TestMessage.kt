package de.gleex.pltcmd.game.engine.attributes.goals.testimplementations

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.ElementType
import org.hexworks.amethyst.api.Message
import org.hexworks.amethyst.api.entity.Entity

data class TestMessage(
        val value: Int,
        override val source: Entity<ElementType, GameContext> = goalTestEntity,
        override val context: GameContext = testGameContext
) : Message<GameContext>