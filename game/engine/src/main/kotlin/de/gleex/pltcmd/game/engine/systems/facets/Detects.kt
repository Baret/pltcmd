package de.gleex.pltcmd.game.engine.systems.facets

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.attributes.VisionAttribute
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.game.engine.extensions.logIdentifier
import de.gleex.pltcmd.game.engine.messages.DetectEntities
import de.gleex.pltcmd.game.engine.messages.DetectedEntity
import de.gleex.pltcmd.model.signals.core.SignalStrength
import de.gleex.pltcmd.model.signals.vision.Visibility
import de.gleex.pltcmd.model.signals.vision.visibility
import kotlinx.coroutines.runBlocking
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.Response
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.cobalt.logging.api.LoggerFactory

/**
 * Handles the [DetectEntities] message. It gets a set of possibly visible entities and calculates the actual
 * visibility using source's vision.
 */
object Detects : BaseFacet<GameContext, DetectEntities>(
    DetectEntities::class,
    VisionAttribute::class,
    PositionAttribute::class
) {
    private val log = LoggerFactory.getLogger(Detects::class)

    override suspend fun receive(message: DetectEntities): Response {
        val (visibleEntities, seeing, context) = message
        seeing.clearView()
        visibleEntities
            .mapNotNull { seen -> createDetectedCommand(seen, seeing, context) }
            .apply {
                runBlocking {
                    forEach {
                        seeing.receiveMessage(it)
                    }
                }
            }
        return Consumed
    }

    private fun createDetectedCommand(
        seen: PositionableEntity,
        seeing: SeeingEntity,
        context: GameContext
    ): DetectedEntity? {
        val seenPosition = seen.currentPosition
        val visionStrength = seeing.vision.at(seenPosition)
        val visibility = visionStrength.visibility
        return if (visibility != Visibility.NONE) {
            logSeen(seeing, seen, visionStrength)
            seeing.sighted(seen, visibility)
            DetectedEntity(seen, visibility, seeing, context)
        } else {
            null
        }
    }

    private fun logSeen(
        seeing: SeeingEntity,
        seen: PositionableEntity,
        visibility: SignalStrength
    ) {
        if (log.isDebugEnabled()) {
            val who = seeing.logIdentifier.padEnd(12)
            val what = seen.logIdentifier.padEnd(12)
            val where = seen.currentPosition.toString().padEnd(12)
            val how = visibility.asRatio()
            log.debug("$who sees $what at $where with signal strength $how")
        }
    }

}
