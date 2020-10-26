package de.gleex.pltcmd.game.ui.sound.speech.effects

/**
 * This is a simple model class to create the String representation of an [marytts.signalproc.effects.AudioEffect].
 *
 * We could directly use the implementations of that class but they have a horrible API and we can only set them
 * in [marytts.LocalMaryInterface.effects] as String. That's why we have this simple data class.
 *
 * To be parsable by MaryTTS it has to be in the form of either:
 * - "EffectName" when it has no parameters or
 * - "EffectName(parameter1:value1;parameter2:value2)"
 */
internal data class Effect(
        val name: String,
        val parameters: EffectParameterList = EffectParameterList.empty
) {
    override fun toString() = "$name${if (parameters != EffectParameterList.empty) "($parameters)" else ""}"
}