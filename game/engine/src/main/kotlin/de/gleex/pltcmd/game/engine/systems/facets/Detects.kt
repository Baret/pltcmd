package de.gleex.pltcmd.game.engine.systems.facets

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.attributes.VisionAttribute
import de.gleex.pltcmd.game.engine.commands.DetectEntities
import de.gleex.pltcmd.game.engine.commands.DetectedElement
import de.gleex.pltcmd.game.engine.commands.DetectedUnknown
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.model.signals.core.SignalStrength
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.runBlocking
import org.hexworks.amethyst.api.Command
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.Response
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.amethyst.api.entity.EntityType
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

    override suspend fun executeCommand(command: Command<out EntityType, GameContext>): Response =
        command.responseWhenCommandIs<GameContext, DetectEntities> { (visibleEntities, seeing) ->
            runBlocking {
                val handleCommands = produce {
                    visibleEntities.forEach { seen ->
                        var handleCommand: Command<Seeing, GameContext>? = null
                        // basic information is always available
                        val seenType = seen.type
                        val seenPosition = seen.currentPosition

                        // details of the entity type are only available if seen is clearly visible
                        val visibility: SignalStrength = seeing.vision.at(seenPosition)
                        if (visibility.strength >= 0.4) {
                            when (seenType) {
                                ElementType -> {
                                    val seenElement = seen as ElementEntity
                                    logSeen(seeing, seenPosition, visibility) { seenElement.callsign.name.padEnd(25) }
                                    handleCommand = DetectedElement(seenElement, seeing, command.context)
                                }
                                else        -> log.warn("Detected entity type '$seenType' is not handled!")
                            }
                        } else {
                            logSeen(seeing, seenPosition, visibility) { "something" }
                            handleCommand = DetectedUnknown(seen, seeing, command.context)
                        }
                        handleCommand?.let { send(it) }
                    }
                }
                handleCommands.consumeEach { seeing.executeCommand(it) }
            }
            Consumed
        }

    private fun logSeen(
        seeing: SeeingEntity,
        seenPosition: Coordinate,
        visibility: SignalStrength,
        seenText: () -> String
    ) {
        if (log.isDebugEnabled() && seeing.type == ElementType) {
            log.debug(
                "${(seeing as ElementEntity).callsign.name.padEnd(25)} sees $seenText at ${
                    seenPosition.toString()
                        .padEnd(12)
                } with signal strength \t${visibility}"
            )
        }
    }

}
