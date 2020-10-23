package de.gleex.pltcmd.game.ui.sound.speech.effects

data class EffectParameterList(
        val parameters: List<EffectParameter<*>>
) {
    override fun toString() = parameters.joinToString(";")
    companion object {
        val empty = EffectParameterList(emptyList())
    }
}
