package de.gleex.pltcmd.game.ui.sound.speech.effects

data class Effect(
        val name: String,
        val parameters: EffectParameterList = EffectParameterList.empty
) {
    override fun toString() = "$name${if (parameters != EffectParameterList.empty) "($parameters)" else ""}"
}