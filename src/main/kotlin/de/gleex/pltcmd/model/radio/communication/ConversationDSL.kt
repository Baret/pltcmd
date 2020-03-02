package de.gleex.pltcmd.model.radio.communication

import de.gleex.pltcmd.model.elements.CallSign

fun conversation(sender: CallSign, receiver: CallSign, builder: ConversationBuilder.() -> Unit): Conversation {
    return ConversationBuilder(sender, receiver).apply(builder).build()
}