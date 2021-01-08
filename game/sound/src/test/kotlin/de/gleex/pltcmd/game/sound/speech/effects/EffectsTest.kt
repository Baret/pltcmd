package de.gleex.pltcmd.game.sound.speech.effects

import io.kotest.core.spec.style.WordSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import marytts.signalproc.effects.*

class EffectsTest: WordSpec({
    "The toString representation of our effects" should {
        forAll(
                row(
                        Effects.jetPilot(),
                        JetPilotEffect()),
                row(
                        Effects.chorus(199.0, 0.12, 244.0, 0.44, 91.0, 9.1),
                        ChorusEffectBase()
                                .apply { setParams("delay1:199;amp1:0.12;delay2:244;amp2:0.44;delay3:91;amp3:9.1") }
                ),
                row(
                        Effects.rate(19.99999),
                        HMMDurationScaleEffect()
                                .apply { setParams("durScale:19.99999") }
                ),
                row(
                        Effects.f0Add(-42.01),
                        HMMF0AddEffect()
                                .apply { setParams("f0Add:-42.01") }
                ),
                row(
                        Effects.f0Scale(2.0),
                        HMMF0ScaleEffect()
                                .apply { setParams("f0Scale:2.0") }
                ),
                row(
                        Effects.firFilter(2.0, 420.000, 1337.101),
                        FilterEffectBase(420.0, 1337.101, 16000, 2)
                                .apply { setParams("type:2;fc1:420.0;fc2:1337.101") }
                ),
                row(
                        Effects.robot(99.1),
                        RobotiserEffect()
                                .apply { setParams("amount:99.1") }
                ),
                row(
                        Effects.stadium(100.0),
                        StadiumEffect()
                                .apply { setParams("amount:100.0") }
                ),
                row(
                        Effects.volume(1.0000000000000000000001),
                        VolumeEffect()
                                .apply { setParams("amount:1.0000000000000000000001") }
                ),
                row(
                        Effects.whisper(1234.5677),
                        LpcWhisperiserEffect()
                                .apply { setParams("amount:1234.5677") }
                ),
                row(
                        Effects.tractScaler(1.51),
                        VocalTractLinearScalerEffect()
                                .apply { setParams("amount:1.51") }
                )
        ) { effect, expectedEffect ->
                "be equal to the original for effect $effect" {
                    effect.toString() shouldBe expectedEffect.fullEffectAsString
                }
            }
        }
})