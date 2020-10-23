package de.gleex.pltcmd.game.ui.sound.speech.effects

object Effects {
    fun jetPilot() = effect("JetPilot")

    fun volume(amount: Double = 2.0) =
            effect("Volume",
                    "amount" to amount)

    fun tractScaler(amount: Double = 1.5) {
        effect("TractScaler",
                "amount" to amount)
    }

    fun f0Scale(f0Scale: Double = 2.0) =
            effect("F0Scale",
                    "f0Scale" to f0Scale)

    fun f0Add(f0Add: Double = 50.0) =
            effect("F0Add",
                    "f0Add" to f0Add)

    fun rate(durScale: Double = 1.5) =
            effect("Rate",
                    "durScale" to durScale)

    fun robot(amount: Double = 100.0) =
            effect("Robot",
                    "amount" to amount)

    fun whisper(amount: Double = 100.0) =
            effect("Whisper",
                    "amount" to amount)

    fun stadium(amount: Double = 100.0) =
            effect("Stadium",
                    "amount" to amount)

    fun firFilter(
            type: Int = 3,
            fc1: Double = 500.0,
            fc2: Double = 2000.0,
    ) =
            effect("FIRFilter",
                    "type" to type,
                    "fc1" to fc1,
                    "fc2" to fc2)

    fun chorus(
            delay1: Int = 466,
            amp1: Double = 0.54,
            delay2: Int = 600,
            amp2: Double = -0.10,
            delay3: Int = 250,
            amp3: Double = 0.30
    ) =
            effect("Chorus",
                    "delay1" to delay1,
                    "amp1" to amp1,
                    "delay2" to delay2,
                    "amp2" to amp2,
                    "delay3" to delay3,
                    "amp3" to amp3)

    private fun effect(name: String, vararg parameters: Pair<String, Any>): Effect {
        return Effect(
                name,
                EffectParameterList(
                        parameters.map { (parameterName, parameterValue) ->
                            EffectParameter(parameterName, parameterValue)
                        }))
    }
}