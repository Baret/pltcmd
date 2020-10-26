package de.gleex.pltcmd.game.ui.sound.speech.effects

/**
 * A list of [Effect]s. This model class actually creates the string needed by [marytts.LocalMaryInterface.effects]
 * to set the currently active effects.
 *
 * Its string representation is "Effect1+Effect2(p:v)".
 */
internal data class EffectList(
        val effects: List<Effect>
) {
    override fun toString() = effects.joinToString("+")

    companion object {
        val none = EffectList.of()

        fun of(vararg effects: Effect) =
                EffectList(effects.toList())
    }
}