package de.gleex.pltcmd.game.engine.facets

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.DestinationAttribute
import de.gleex.pltcmd.game.engine.entities.ElementType
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.Command
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.amethyst.api.entity.Entity
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.logging.api.LoggerFactory

/** Command to provide entities a destination **/
data class MoveTo(
        val destination: Coordinate,
        override val context: GameContext,
        override val source: Entity<ElementType, GameContext>
) : Command<ElementType, GameContext>

class SetDestination : BaseFacet<GameContext>() {
    companion object {
        val log = LoggerFactory.getLogger(SetDestination::class)
    }

    override suspend fun executeCommand(command: Command<out EntityType, GameContext>) =
            command.responseWhenCommandIs(MoveTo::class) { (destination, context, entity) ->
                getOrAddDestination(entity, destination)
                        .coordinate.value = destination
                log.debug("Set destination for ${entity.name} to $destination")
                Consumed
            }

    private fun getOrAddDestination(entity: Entity<ElementType, GameContext>, destination: Coordinate): DestinationAttribute {
        return entity.findAttribute(DestinationAttribute::class)
                .orElseGet {
                    val destAttr = DestinationAttribute(destination)
                    entity.asMutableEntity()
                            .addAttribute(destAttr)
                    destAttr
                }
    }

}