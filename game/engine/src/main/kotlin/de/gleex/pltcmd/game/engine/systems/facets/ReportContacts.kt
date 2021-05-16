package de.gleex.pltcmd.game.engine.systems.facets

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.FactionAttribute
import de.gleex.pltcmd.game.engine.attributes.RadioAttribute
import de.gleex.pltcmd.game.engine.attributes.memory.Memory
import de.gleex.pltcmd.game.engine.attributes.memory.elements.*
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.game.engine.extensions.logIdentifier
import de.gleex.pltcmd.game.engine.messages.DetectedEntity
import de.gleex.pltcmd.game.options.GameOptions
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.faction.Affiliation
import de.gleex.pltcmd.model.radio.communication.Conversation
import de.gleex.pltcmd.model.radio.communication.Conversations
import de.gleex.pltcmd.model.signals.vision.Visibility
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.util.knowledge.KnowledgeGrade
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.Pass
import org.hexworks.amethyst.api.Response
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.cobalt.logging.api.LoggerFactory

/**
 * Sends a contact report to the hq when something is detected.
 */
object ReportContacts : BaseFacet<GameContext, DetectedEntity>(
    DetectedEntity::class, Memory::class, RadioAttribute::class, FactionAttribute::class
) {

    private val log = LoggerFactory.getLogger(ReportContacts::class)

    override suspend fun receive(message: DetectedEntity): Response {
        val detected = message
        val communicating = detected.source as CommunicatingEntity
        // create contact
        return detected.entity.asElementEntity {
            communicating hasContactWith it
        }
            .map { contact ->
                // remember contact
                // TODO remember at end of the tick in an own facet?
                val knownContact = contact knownBy detected
                val wasNotYetKnown = communicating.memory.knownContacts.update(knownContact)
                // send contact
                return@map if (wasNotYetKnown) {
                    reportContact(communicating, knownContact, detected.context)
                } else {
                    Pass
                }
            }
            .orElse(Pass)
    }

    fun reportContact(reporter: CommunicatingEntity, toReport: KnownContact, context: GameContext): Response {
        val affiliationToReport = toReport.affiliation
        return when {
            // unidentified faction
            affiliationToReport.isEmpty()                    -> reportUnknown(reporter, toReport, context)
            affiliationToReport.get() == Affiliation.Hostile -> reportHostile(reporter, toReport, context)
            // neutral and friends are not reported over the radio
            else                                             -> Pass
        }
    }

    fun reportUnknown(reporter: CommunicatingEntity, toReport: KnownContact, context: GameContext): Response {
        sendReport(reporter, "unknown ${toReport.description}", toReport.position, context)
        return Consumed
    }

    fun reportHostile(reporter: CommunicatingEntity, toReport: KnownContact, context: GameContext): Response {
        sendReport(reporter, "hostile ${toReport.description}", toReport.position, context)
        return Consumed
    }

    fun sendReport(reporter: CommunicatingEntity, what: String, at: Coordinate, context: GameContext) {
        // TODO Does non player controlled elements need contact reports?
        if (reporter.affiliationTo(context.playerFaction) != Affiliation.Self) {
            log.trace("not reporting contact of non player faction of ${reporter.logIdentifier}: $what at $at")
            return
        }
        val hq = CallSign(GameOptions.commandersCallSign)
        log.debug("Reporting contact of ${reporter.logIdentifier} to ${hq.name}: $what at $at")
        val report: Conversation = Conversations.Messages.contact(reporter.radioCallSign, hq, what, at)
        reporter.startConversation(report)
    }

}

internal infix fun ContactData.knownBy(message: DetectedEntity) = KnownContact(
    origin = this,
    when (message.visibility) {
        Visibility.NONE -> KnowledgeGrade.NONE
        Visibility.POOR -> KnowledgeGrade.MEDIUM
        Visibility.GOOD -> KnowledgeGrade.FULL
    }
)