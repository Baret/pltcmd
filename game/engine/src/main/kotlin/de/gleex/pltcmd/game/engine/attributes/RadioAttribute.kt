package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.game.communication.RadioCommunicator
import de.gleex.pltcmd.model.elements.Element
import de.gleex.pltcmd.model.radio.RadioSender
import org.hexworks.amethyst.api.Attribute

/**
 * The radio that is used to send and receive transmissions.
 * @see RadioCommunicator
 **/
internal data class RadioAttribute(val communicator: RadioCommunicator) : Attribute {

    constructor(element: Element, radioSender: RadioSender) : this(RadioCommunicator(element.callSign, radioSender))

}