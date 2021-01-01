package de.gleex.pltcmd.game.engine.systems.facets

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.attributes.VisionAttribute
import de.gleex.pltcmd.game.engine.commands.DetectEntities
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.model.elements.Affiliation
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.Message
import org.hexworks.amethyst.api.Pass
import org.hexworks.amethyst.api.Response
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.amethyst.api.extensions.responseWhenCommandIs
import org.hexworks.cobalt.logging.api.LoggerFactory

/**
 * Handles the [DetectEntities] command. It gets a set of possibly visible entities and calculates the actual
 * visibility using source's vision.
 */
object Detects : BaseFacet<GameContext>(
        VisionAttribute::class,
        PositionAttribute::class
) {
    private val log = LoggerFactory.getLogger(Detects::class)

    override suspend fun executeCommand(command: Message<GameContext>): Response =
            command.responseWhenCommandIs<GameContext, DetectEntities> { (visibleEntities, source) ->
                if (visibleEntities.isEmpty()) {
                    Pass
                }
                val seeingElement = source as ElementEntity
                visibleEntities.forEach { seen ->
                    // TODO: Implement actual behavior of detecting things and reacting to them (i.e. do a contact report) (#130)
                    if (seeingElement.affiliation == Affiliation.Friendly) {
                        val seenElement = seen as ElementEntity
                        val targetLocation = seenElement.currentPosition
                        log.debug("${seeingElement.callsign.name.padEnd(25)} sees ${seenElement.callsign.name.padEnd(25)} at ${
                            targetLocation.toString()
                                    .padEnd(12)
                        } with signal strength \t${seeingElement.vision.at(targetLocation)}")
                    }
                }
                Consumed
            }
}