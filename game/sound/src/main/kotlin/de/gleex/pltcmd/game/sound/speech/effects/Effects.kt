package de.gleex.pltcmd.game.sound.speech.effects

import marytts.signalproc.effects.*

/**
 * This object creates [Effect] model objects that can be used to set the [marytts.LocalMaryInterface.effects] string.
 * 
 * There are actual model objects coming with Mary but they are a PITA to use.
 */
internal object Effects {
    /**
     * Creates an [Effect] representing [JetPilotEffect].
     */
    fun jetPilot() = effect("JetPilot")

    /**
     * Creates an [Effect] representing [VolumeEffect].
     */
    fun volume(amount: Double = 2.0) =
            effect("Volume",
                    "amount" to amount)

    /**
     * Creates an [Effect] representing [VocalTractLinearScalerEffect].
     */
    fun tractScaler(amount: Double = 1.5) =
        effect("TractScaler",
                "amount" to amount)

    /**
     * Creates an [Effect] representing [HMMF0ScaleEffect].
     */
    fun f0Scale(f0Scale: Double = 2.0) =
            effect("F0Scale",
                    "f0Scale" to f0Scale)

    /**
     * Creates an [Effect] representing [HMMF0AddEffect].
     */
    fun f0Add(f0Add: Double = 50.0) =
            effect("F0Add",
                    "f0Add" to f0Add)

    /**
     * Creates an [Effect] representing [HMMDurationScaleEffect].
     */
    fun rate(durScale: Double = 1.5) =
            effect("Rate",
                    "durScale" to durScale)

    /**
     * Creates an [Effect] representing [RobotiserEffect].
     */
    fun robot(amount: Double = 100.0) =
            effect("Robot",
                    "amount" to amount)

    /**
     * Creates an [Effect] representing [LpcWhisperiserEffect].
     */
    fun whisper(amount: Double = 100.0) =
            effect("Whisper",
                    "amount" to amount)

    /**
     * Creates an [Effect] representing [StadiumEffect].
     */
    fun stadium(amount: Double = 100.0) =
            effect("Stadium",
                    "amount" to amount)

    /**
     * Creates an [Effect] representing [FilterEffectBase].
     *
     * The possible values for [type] are:
     * - [FilterEffectBase.NULL_FILTER]
     * - [FilterEffectBase.LOWPASS_FILTER]
     * - [FilterEffectBase.HIGHPASS_FILTER]
     * - [FilterEffectBase.BANDPASS_FILTER] (default)
     * - [FilterEffectBase.BANDREJECT_FILTER]
     *
     */
    fun firFilter(
            type: Double = 3.0,
            fc1: Double = 500.0,
            fc2: Double = 2000.0,
    ) =
            effect("FIRFilter",
                    "type" to type,
                    "fc1" to fc1,
                    "fc2" to fc2)

    /**
     * Creates an [Effect] representing [ChorusEffectBase].
     */
    fun chorus(
            delay1: Double = 466.0,
            amp1: Double = 0.54,
            delay2: Double = 600.0,
            amp2: Double = -0.10,
            delay3: Double = 250.0,
            amp3: Double = 0.30
    ) =
            effect("Chorus",
                    "delay1" to delay1,
                    "amp1" to amp1,
                    "delay2" to delay2,
                    "amp2" to amp2,
                    "delay3" to delay3,
                    "amp3" to amp3)

    /**
     * Simple builder method to create an [Effect] from a name and some key-value pairs as parameters.
     */
    private fun effect(name: String, vararg parameters: Pair<String, Double>): Effect {
        return Effect(
                name,
                EffectParameterList(
                        parameters.map { (parameterName, parameterValue) ->
                            EffectParameter(parameterName, parameterValue)
                        }))
    }
}