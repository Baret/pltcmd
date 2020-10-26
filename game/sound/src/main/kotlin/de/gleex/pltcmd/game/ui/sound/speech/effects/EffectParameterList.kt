package de.gleex.pltcmd.game.ui.sound.speech.effects

/**
 * A list of [EffectParameter]s.
 *
 * It's string representation is "effect1:value1;effect2:value2".
 */
internal data class EffectParameterList(
        val parameters: List<EffectParameter>
) {
    override fun toString() = parameters.joinToString(";")
    companion object {
        val empty = EffectParameterList(emptyList())
    }
}
