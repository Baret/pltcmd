package de.gleex.pltcmd.game.engine.systems.facets

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.commands.ConversationCommand
import de.gleex.pltcmd.game.engine.commands.DetectedElement
import de.gleex.pltcmd.game.engine.commands.DetectedUnknown
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.model.elements.Affiliation
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.Conversation
import de.gleex.pltcmd.model.radio.communication.Conversations
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.Command
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.Pass
import org.hexworks.amethyst.api.Response
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.amethyst.api.entity.EntityType

/**
 * Sends a contact report to the hq when something is detected.
 */
object ReportContacts : BaseFacet<GameContext>() {

    override suspend fun executeCommand(command: Command<out EntityType, GameContext>): Response {
        if (command.source.type !is Communicating) {
            return Pass
        }
        val communicating = command.source as CommunicatingEntity
        val context = command.context
        when (command) {
            is DetectedUnknown -> reportUnknown(communicating, command.entity, context)
            is DetectedElement -> reportElement(communicating, command.element, context)
        }
        return Consumed
    }

    suspend fun reportUnknown(reporter: CommunicatingEntity, toReport: PositionableEntity, context: GameContext) {
        sendReport(reporter, "unknown", toReport.currentPosition, context)
    }

    suspend fun reportElement(reporter: CommunicatingEntity, toReport: ElementEntity, context: GameContext) {
        sendReport(reporter, toReport.element.description, toReport.currentPosition, context)
    }

    suspend fun sendReport(reporter: CommunicatingEntity, what: String, at: Coordinate, context: GameContext) {
        // TODO where does the receiver call sign come from? Is it the fixed superior element? Remembered from last conversation?
        if ((reporter as ElementEntity).affiliation != Affiliation.Friendly) {
            return
        }
        val hq = CallSign("HQ")
        val report: Conversation = Conversations.Messages.contact(reporter.radioCallSign, hq, what, at)
        val reportCommand = ConversationCommand(report, context, reporter)
        reporter.sendCommand(reportCommand)
    }

}