package de.gleex.pltcmd.game.engine.systems.facets

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.knowledge.LocatedContact
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.game.engine.messages.DetectedEntity
import de.gleex.pltcmd.game.options.GameOptions
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.faction.Affiliation
import de.gleex.pltcmd.model.radio.communication.Conversation
import de.gleex.pltcmd.model.radio.communication.Conversations
import de.gleex.pltcmd.model.signals.vision.Visibility
import de.gleex.pltcmd.model.world.WorldArea
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

    override suspend fun receive(message: DetectedEntity): Response {
        val detected = message
        return if (!detected.increasedVisibility || detected.source.type !is Communicating) {
            Pass
        } else {
            val communicating = detected.source as CommunicatingEntity
            when (detected.visibility) {
                // details of the entity type are only available if seen is clearly visible
                Visibility.GOOD -> reportContact(communicating, detected.contact, detected.context)
                // basic information is always available
                Visibility.POOR -> reportUnknown(communicating, detected.contact, detected.context)
                Visibility.NONE -> Pass
            }
        }
    }

    fun reportContact(reporter: CommunicatingEntity, toReport: LocatedContact, context: GameContext): Response {
        return toReport.contact.faction.fold({
            // unidentified faction
            reportUnknown(reporter, toReport, context)
        }, { faction ->
            if (reporter.affiliationTo(faction) == Affiliation.Hostile) {
                reportElement(reporter, toReport, context)
            } else {
                // neutral and friends are not reported over the radio
                Pass
            }
        })
    }

    fun reportUnknown(reporter: CommunicatingEntity, toReport: LocatedContact, context: GameContext): Response {
        sendReport(reporter, "unknown ${toReport.contact.description}", toReport.roughLocation, context)
        return Consumed
    }

    fun reportElement(reporter: CommunicatingEntity, toReport: LocatedContact, context: GameContext): Response {
        sendReport(reporter, toReport.contact.description, toReport.roughLocation, context)
        return Consumed
    }

    fun sendReport(reporter: CommunicatingEntity, what: String, at: WorldArea, context: GameContext) {
        // TODO Does non player controlled elements need contact reports?
        if (reporter.affiliationTo(context.playerFaction) != Affiliation.Self) {
            return
        }
        val hq = CallSign(GameOptions.commandersCallSign)
        val report: Conversation = Conversations.Messages.contact(reporter.radioCallSign, hq, what, at.description)
        reporter.startConversation(report)
    }

}