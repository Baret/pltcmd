package de.gleex.pltcmd.game.engine.attributes.goals.testimplementations

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.EntitySet
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.ElementType
import de.gleex.pltcmd.game.ticks.TickId
import de.gleex.pltcmd.model.world.WorldMap
import io.mockk.mockkClass
import org.hexworks.amethyst.api.newEntityOfType
import kotlin.random.Random

val goalTestEntity: ElementEntity = newEntityOfType(ElementType) {
    // no attributes etc. needed yet
}

val testGameContext: GameContext = GameContext(
        currentTick = TickId(12),
        world = mockkClass(WorldMap::class),
        entities = EntitySet(),
        random = Random
)