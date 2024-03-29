package de.gleex.pltcmd.game.engine.systems.facets

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.FactionAttribute
import de.gleex.pltcmd.game.engine.attributes.RadioAttribute
import de.gleex.pltcmd.game.engine.attributes.goals.RadioGoal
import de.gleex.pltcmd.game.engine.attributes.memory.Memory
import de.gleex.pltcmd.game.engine.attributes.memory.elements.KnownContact
import de.gleex.pltcmd.game.engine.attributes.memory.elements.description
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.game.engine.extensions.logIdentifier
import de.gleex.pltcmd.game.engine.messages.DetectedEntity
import de.gleex.pltcmd.game.options.GameOptions
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.faction.Affiliation
import de.gleex.pltcmd.model.radio.communication.Conversation
import de.gleex.pltcmd.model.radio.communication.Conversations
import de.gleex.pltcmd.model.signals.vision.Visibility
import de.gleex.pltcmd.util.knowledge.KnowledgeGrade
import de.gleex.pltcmd.util.measure.compass.bearing.Bearing
import mu.KotlinLogging
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.Pass
import org.hexworks.amethyst.api.Response
import org.hexworks.amethyst.api.base.BaseFacet

private val log = KotlinLogging.logger {}

/**
 * Sends a contact report to the hq when something is detected.
 */
object ReportContacts : BaseFacet<GameContext, DetectedEntity>(
    DetectedEntity::class, Memory::class, RadioAttribute::class, FactionAttribute::class
) {

    override suspend fun receive(message: DetectedEntity): Response {
        val reporter: ElementEntity? = message.source.asElementEntity { it }
        val detectedElement = message.entity.asElementEntity { it }
        if (reporter != null && detectedElement != null) {
            val contact = KnownContact(reporter, detectedElement, message.knowledgeGrade)
            val hasNewKnowledge = reporter.rememberContact(contact)
            if(hasNewKnowledge) {
                return reportContact(reporter, contact, message.context)
            }
        }
        return Pass
    }

    private fun reportContact(reporter: ElementEntity, contact: KnownContact, context: GameContext): Response {
        return when (contact.affiliation) {
            Affiliation.Unknown, Affiliation.Hostile -> {
                sendReport(reporter, contact.description, reporter.currentPosition bearingTo contact.position, context)
                Consumed
            }
            // neutral and friends are not reported over the radio
            else                                     -> Pass
        }
    }

    private fun sendReport(reporter: ElementEntity, what: String, at: Bearing, context: GameContext) {
        // TODO Does non player controlled elements need contact reports? -> sure! But until we have channels (#42) we keep this workaround
        if (reporter.affiliationTo(context.playerFaction) != Affiliation.Self) {
            log.trace { "not reporting contact of non player faction of ${reporter.logIdentifier}: $what at $at" }
            return
        }
        val hq = CallSign(GameOptions.commandersCallSign)
        log.debug { "Reporting contact of ${reporter.logIdentifier} to ${hq.name}: $what at $at" }
        val report: Conversation = Conversations.Messages.contact(reporter.radioCallSign, hq, what, at)
        reporter.commandersIntent.butNow(RadioGoal(report))
    }

    private val DetectedEntity.knowledgeGrade: KnowledgeGrade
        get() = when (visibility) {
            Visibility.NONE -> KnowledgeGrade.NONE
            Visibility.POOR -> KnowledgeGrade.MEDIUM
            Visibility.GOOD -> KnowledgeGrade.FULL
        }
}
