package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.game.communication.RadioCommunicator
import org.hexworks.amethyst.api.Attribute

/**
 * The radio that is used to send and receive transmissions.
 * @see RadioCommunicator
 **/
internal data class RadioAttribute(val communicator: RadioCommunicator) : Attribute