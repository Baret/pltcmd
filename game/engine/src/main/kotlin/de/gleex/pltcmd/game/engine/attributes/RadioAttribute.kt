package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.model.radio.RadioSender
import org.hexworks.amethyst.api.Attribute

/**
 * The radio that is used to send and receive transmissions.
 * @see RadioSender
 **/
internal data class RadioAttribute(val radio: RadioSender) : Attribute