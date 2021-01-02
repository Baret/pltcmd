package de.gleex.pltcmd.game.engine.systems.facets

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.game.engine.messages.DetectedEntity
import de.gleex.pltcmd.model.elements.Affiliation
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.Conversation
import de.gleex.pltcmd.model.radio.communication.Conversations
import de.gleex.pltcmd.model.signals.vision.Visibility
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.Pass
import org.hexworks.amethyst.api.Response
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.cobalt.logging.api.LoggerFactory

/**
 * Sends a contact report to the hq when something is detected.
 */
object ReportContacts : BaseFacet<GameContext, DetectedEntity>(DetectedEntity::class) {

    private val log = LoggerFactory.getLogger(ReportContacts::class)

    override suspend fun receive(detected: DetectedEntity): Response {
        return if (!detected.isNew || detected.source.type !is Communicating) {
            Pass
        } else {
            val communicating = detected.source as CommunicatingEntity
            when (detected.visibility) {
                // details of the entity type are only available if seen is clearly visible
                Visibility.GOOD -> reportContact(communicating, detected.entity, detected.context)
                // basic information is always available
                Visibility.POOR -> reportUnknown(communicating, detected.entity, detected.context)
                Visibility.NONE -> Pass
            }
        }
    }

    fun reportContact(reporter: CommunicatingEntity, toReport: PositionableEntity, context: GameContext): Response {
        return when (toReport.type) {
            ElementType -> {
                val elementToReport = toReport as ElementEntity
                if (elementToReport.affiliation == Affiliation.Hostile) {
                    reportElement(reporter, elementToReport, context)
                    Consumed
                } else {
                    // neutral and friends are not reported over the radio
                    Pass
                }
            }
            else        -> {
                log.warn("Not reporting entity type '${toReport.type}'!")
                Pass
            }
        }
    }

    fun reportUnknown(reporter: CommunicatingEntity, toReport: PositionableEntity, context: GameContext): Response {
        sendReport(reporter, "unknown", toReport.currentPosition, context)
        return Consumed
    }

    fun reportElement(reporter: CommunicatingEntity, toReport: ElementEntity, context: GameContext): Response {
        sendReport(reporter, toReport.element.description, toReport.currentPosition, context)
        return Consumed
    }

    fun sendReport(reporter: CommunicatingEntity, what: String, at: Coordinate, context: GameContext) {
        // TODO where does the receiver call sign come from? Is it the fixed superior element (of the faction #62)? Remembered from last conversation?
        if ((reporter as ElementEntity).affiliation != Affiliation.Friendly) {
            return
        }
        val hq = CallSign("HQ")
        val report: Conversation = Conversations.Messages.contact(reporter.radioCallSign, hq, what, at)
        reporter.startConversation(report)
    }

}