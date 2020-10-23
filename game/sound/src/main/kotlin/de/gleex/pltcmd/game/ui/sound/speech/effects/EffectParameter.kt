package de.gleex.pltcmd.game.ui.sound.speech.effects

data class EffectParameter<T>(
        val name: String,
        val value: T
) {
    override fun toString() = "$name:$value"
}
