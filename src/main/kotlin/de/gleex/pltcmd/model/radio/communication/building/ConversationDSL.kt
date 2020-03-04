package de.gleex.pltcmd.model.radio.communication.building

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.Conversation

fun conversation(sender: CallSign, receiver: CallSign, builder: ConversationBuilder.() -> Unit): Conversation {
    return ConversationBuilder(sender, receiver)
            .apply(builder).build()
}