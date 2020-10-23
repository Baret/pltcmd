package de.gleex.pltcmd.game.ui.sound.speech.effects

data class EffectList(
        val effects: List<Effect>
) {
    override fun toString() = effects.joinToString("+")

    companion object {
        val none = EffectList.of()

        fun of(vararg effects: Effect) =
                EffectList(effects.toList())
    }
}