package de.gleex.pltcmd.game.engine.systems.facets

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.game.engine.messages.DetectedEntity
import de.gleex.pltcmd.game.options.GameOptions
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.faction.Affiliation
import de.gleex.pltcmd.model.radio.communication.Conversation
import de.gleex.pltcmd.model.radio.communication.Conversations
import de.gleex.pltcmd.model.signals.vision.Visibility
import de.gleex.pltcmd.util.measure.compass.bearing.Bearing
import mu.KotlinLogging
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.Pass
import org.hexworks.amethyst.api.Response
import org.hexworks.amethyst.api.base.BaseFacet

/**
 * Sends a contact report to the hq when something is detected.
 */
object ReportContacts : BaseFacet<GameContext, DetectedEntity>(DetectedEntity::class) {

    private val log = KotlinLogging.logger {}

    override suspend fun receive(message: DetectedEntity): Response {
        val detected = message
        return if (!detected.increasedVisibility || detected.source.type !is Communicating) {
            Pass
        } else {
            val reportingElement = detected.source as ElementEntity
            when (detected.visibility) {
                // details of the entity type are only available if seen is clearly visible
                Visibility.GOOD -> reportContact(reportingElement, detected.entity, detected.context)
                // basic information is always available
                Visibility.POOR -> reportUnknown(reportingElement, detected.entity, detected.context)
                Visibility.NONE -> Pass
            }
        }
    }

    fun reportContact(reporter: ElementEntity, toReport: PositionableEntity, context: GameContext): Response {
        return when (toReport.type) {
            ElementType -> {
                val elementToReport = toReport as ElementEntity
                if (reporter.affiliationTo(elementToReport) == Affiliation.Hostile) {
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

    fun reportUnknown(reporter: ElementEntity, toReport: PositionableEntity, context: GameContext): Response {
        sendReport(reporter, "unknown", reporter.currentPosition bearingTo toReport.currentPosition, context)
        return Consumed
    }

    fun reportElement(reporter: ElementEntity, toReport: ElementEntity, context: GameContext): Response {
        sendReport(reporter, toReport.element.description, reporter.currentPosition bearingTo toReport.currentPosition, context)
        return Consumed
    }

    fun sendReport(reporter: CommunicatingEntity, what: String, at: Bearing, context: GameContext) {
        // TODO report to own faction #62 only. Does non player controlled elements need contact reports?
        if (reporter.affiliationTo(context.playerFaction) != Affiliation.Self) {
            return
        }
        val hq = CallSign(GameOptions.commandersCallSign)
        val report: Conversation = Conversations.Messages.contact(reporter.radioCallSign, hq, what, at)
        reporter.startConversation(report)
    }

}