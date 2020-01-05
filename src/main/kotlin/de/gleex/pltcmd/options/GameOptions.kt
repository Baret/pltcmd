package de.gleex.pltcmd.options

import de.gleex.pltcmd.model.radio.PercentageReducingAttenuation

/**
 * Options that change the behaviour of the game.
 */
object GameOptions {
    var attenuationModel = PercentageReducingAttenuation()

    const val SECTORS_COUNT_H = 1
    const val SECTORS_COUNT_V = 1
}