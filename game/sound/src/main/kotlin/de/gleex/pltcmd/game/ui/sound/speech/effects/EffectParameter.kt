package de.gleex.pltcmd.game.ui.sound.speech.effects

/**
 * A key-value pair representing a parameter of an [Effect].
 *
 * The string form of it is "key:value".
 */
internal data class EffectParameter(
        val name: String,
        val value: Double
) {
    override fun toString() = "$name:$value"
}
