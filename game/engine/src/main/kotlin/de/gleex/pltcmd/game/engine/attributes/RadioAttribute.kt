package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.model.radio.RadioSender
import de.gleex.pltcmd.model.radio.communication.RadioCommunicator
import org.hexworks.amethyst.api.base.BaseAttribute


/**
 * The radio officer that communicates by sending and receiving transmissions.
 * @see RadioSender
 **/
internal class RadioAttribute(
        val communicator: RadioCommunicator
) : BaseAttribute()
