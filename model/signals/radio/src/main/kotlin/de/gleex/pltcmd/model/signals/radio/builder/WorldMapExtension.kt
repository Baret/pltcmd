package de.gleex.pltcmd.model.signals.radio.builder

import de.gleex.pltcmd.model.signals.core.Signal
import de.gleex.pltcmd.model.signals.radio.RadioPower
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateCircle

/**
 * A radio signal carries a message. It has an initial absolute power depending on the sending radio.
 * While traveling over or through terrain it loses power, which makes it harder to understand ("decode")
 * the message.
 */
fun WorldMap.radioSignalAt(location: Coordinate, power: RadioPower): Signal<RadioPower> =
        Signal(
                location,
                areaOf(CoordinateCircle(location, power.maxRange)),
                power
        )