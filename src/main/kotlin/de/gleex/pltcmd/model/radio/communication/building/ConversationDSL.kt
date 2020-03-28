package de.gleex.pltcmd.model.radio.communication.building

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.Conversation

fun conversation(sender: CallSign, receiver: CallSign, function: ConversationBuilder.() -> Unit): Conversation {
    return ConversationBuilder(sender, receiver)
            .apply(function).build()
}