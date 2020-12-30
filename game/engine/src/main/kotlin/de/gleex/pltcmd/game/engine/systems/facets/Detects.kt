package de.gleex.pltcmd.game.engine.systems.facets

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.attributes.VisionAttribute
import de.gleex.pltcmd.game.engine.commands.DetectEntities
import de.gleex.pltcmd.game.engine.commands.DetectedElement
import de.gleex.pltcmd.game.engine.commands.DetectedUnknown
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.model.signals.core.SignalStrength
import de.gleex.pltcmd.model.world.coordinate.Coordinate
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
        command.responseWhenCommandIs<GameContext, DetectEntities> { (visibleEntities, seeing, context) ->
            visibleEntities
                .mapNotNull { seen -> createDetectedCommand(seen, seeing, context) }
                .apply {
                    runBlocking {
                        forEach {
                            seeing.executeCommand(it)
                        }
                    }
                }
            Consumed
        }

    private fun createDetectedCommand(
        seen: GameEntity<Positionable>,
        seeing: SeeingEntity,
        context: GameContext
    ): Command<Seeing, GameContext>? {
        val seenType = seen.type
        val seenPosition = seen.currentPosition

        val visibility: SignalStrength = seeing.vision.at(seenPosition)
        return when {
            // details of the entity type are only available if seen is clearly visible
            visibility >= 0.4  -> {
                when (seenType) {
                    ElementType -> {
                        val seenElement = seen as ElementEntity
                        logSeen(seeing, seenPosition, visibility) { seenElement.callsign.name }
                        DetectedElement(seenElement, seeing, context)
                    }
                    else        -> {
                        log.warn("Detected entity type '$seenType' is not handled!")
                        null
                    }
                }
            }
            // basic information is always available
            visibility.isAny() -> {
                logSeen(seeing, seenPosition, visibility) { "something" }
                DetectedUnknown(seen, seeing, context)
            }
            else               -> {
                null
            }
        }
    }

    private fun logSeen(
        seeing: SeeingEntity,
        seenPosition: Coordinate,
        visibility: SignalStrength,
        seenText: () -> String
    ) {
        if (log.isDebugEnabled() && seeing.type == ElementType) {
            val who = (seeing as ElementEntity).callsign.name.padEnd(12)
            val what = seenText().padEnd(12)
            val where = seenPosition.toString().padEnd(12)
            val how = visibility.asRatio()
            log.debug("$who sees $what at $where with signal strength $how")
        }
    }

}
