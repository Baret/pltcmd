package de.gleex.pltcmd.game.engine.systems.facets

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.FactionAttribute
import de.gleex.pltcmd.game.engine.attributes.RadioAttribute
import de.gleex.pltcmd.game.engine.attributes.knowledge.ContactsAttribute
import de.gleex.pltcmd.game.engine.attributes.knowledge.LocatedContact
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.game.engine.extensions.logIdentifier
import de.gleex.pltcmd.game.engine.messages.DetectedEntity
import de.gleex.pltcmd.game.options.GameOptions
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.faction.Affiliation
import de.gleex.pltcmd.model.radio.communication.Conversation
import de.gleex.pltcmd.model.radio.communication.Conversations
import de.gleex.pltcmd.model.world.WorldArea
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.Pass
import org.hexworks.amethyst.api.Response
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.cobalt.logging.api.LoggerFactory

/**
 * Sends a contact report to the hq when something is detected.
 */
object ReportContacts : BaseFacet<GameContext, DetectedEntity>(
    DetectedEntity::class, ContactsAttribute::class, RadioAttribute::class, FactionAttribute::class
) {

    private val log = LoggerFactory.getLogger(ReportContacts::class)

    override suspend fun receive(message: DetectedEntity): Response {
        val detected = message
        return if (detected.isKnown) {
            Pass
        } else {
            val communicating = detected.source as CommunicatingEntity
            // TODO remember at end of the tick in an own facet?
            communicating.asRememberingEntity { it.addContact(detected.contact) }
            reportContact(communicating, detected.contact, detected.context)
        }
    }

    fun reportContact(reporter: CommunicatingEntity, toReport: LocatedContact, context: GameContext): Response {
        return toReport.contact.faction.fold({
            // unidentified faction
            reportUnknown(reporter, toReport, context)
        }, { faction ->
            if (reporter.affiliationTo(faction) == Affiliation.Hostile) {
                reportHostile(reporter, toReport, context)
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

    fun reportHostile(reporter: CommunicatingEntity, toReport: LocatedContact, context: GameContext): Response {
        sendReport(reporter, "hostile ${toReport.contact.description}", toReport.roughLocation, context)
        return Consumed
    }

    fun sendReport(reporter: CommunicatingEntity, what: String, at: WorldArea, context: GameContext) {
        // TODO Does non player controlled elements need contact reports?
        if (reporter.affiliationTo(context.playerFaction) != Affiliation.Self) {
            log.trace("not reporting contact of non player faction of ${reporter.logIdentifier}: $what at $at")
            return
        }
        val hq = CallSign(GameOptions.commandersCallSign)
        log.debug("Reporting contact of ${reporter.logIdentifier} to ${hq.name}: $what at $at")
        val report: Conversation = Conversations.Messages.contact(reporter.radioCallSign, hq, what, at.description)
        reporter.startConversation(report)
    }

}