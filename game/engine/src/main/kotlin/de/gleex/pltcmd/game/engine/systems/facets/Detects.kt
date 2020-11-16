package de.gleex.pltcmd.game.engine.systems.facets

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.attributes.VisionAttribute
import de.gleex.pltcmd.game.engine.commands.DetectEntities
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.model.elements.Affiliation
import org.hexworks.amethyst.api.Command
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.Pass
import org.hexworks.amethyst.api.Response
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.amethyst.api.extensions.responseWhenCommandIs
import org.hexworks.cobalt.logging.api.LoggerFactory

object Detects: BaseFacet<GameContext>(
        VisionAttribute::class,
        PositionAttribute::class
) {
    private val log = LoggerFactory.getLogger(Detects::class)

    override suspend fun executeCommand(command: Command<out EntityType, GameContext>): Response =
            command.responseWhenCommandIs<GameContext, DetectEntities> { detectCommand ->
                if(detectCommand.visibleEntities.isNotEmpty()) {
                    detectCommand.visibleEntities.forEach { seen ->
                        val element = detectCommand.source as ElementEntity
                        if(element.affiliation == Affiliation.Friendly) {
                            val seenElement = seen as ElementEntity
                            val targetLocation = seenElement.currentPosition
                            log.info("${element.callsign} sees ${seenElement.callsign} at $targetLocation with signal strength ${element.vision.at(targetLocation)}")
                        }
                    }
                    Consumed
                } else {
                    Pass
                }
            }
}