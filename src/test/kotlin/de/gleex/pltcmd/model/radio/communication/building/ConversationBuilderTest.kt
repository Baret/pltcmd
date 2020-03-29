package de.gleex.pltcmd.model.radio.communication.building

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.transmissions.Transmission
import de.gleex.pltcmd.model.radio.communication.transmissions.context.TransmissionContext
import de.gleex.pltcmd.model.radio.communication.transmissions.decoding.receiver
import de.gleex.pltcmd.model.radio.communication.transmissions.decoding.sender
import de.gleex.pltcmd.model.world.Coordinate
import io.kotlintest.*
import io.kotlintest.specs.WordSpec

class ConversationBuilderTest : WordSpec({
    val context = TransmissionContext(Coordinate(123, 456), 23, 42, 111)
    val sender = CallSign("Test Sender")
    val receiver = CallSign("Test Receiver")
    val underTest = ConversationBuilder(sender, receiver)

    "read back" should {
        val readbackWithoutPlaceholders = underTest.readback("tested %s back", { "placeholder value" })
        "be a response (sender and receiver swapped)" {
            assertSoftly {
                readbackWithoutPlaceholders.sender shouldBe receiver
                readbackWithoutPlaceholders.receiver shouldBe sender
            }
        }
        "establish communications" {
            readbackWithoutPlaceholders should establishCommunications(receiver, sender)
        }
        "have correct template message" {
            readbackWithoutPlaceholders.message shouldBe "Test Sender, this is Test Receiver, roger, tested %s back, out."
        }
        readbackWithoutPlaceholders.transmit(context)
        "have correct transmitted message" {
            readbackWithoutPlaceholders.message shouldBe "Test Sender, this is Test Receiver, roger, tested placeholder value back, out."
        }
    }
})

fun establishCommunications(sender: CallSign, receiver: CallSign) = object : Matcher<Transmission> {
    private val expectedPrefix = "$receiver, this is $sender, "

    override fun test(value: Transmission): MatcherResult {
        return MatcherResult(
                value.message.startsWith(expectedPrefix),
                "Transmission message should start with '$expectedPrefix' but was '${value.message}'",
                "Transmission message should not start with '$expectedPrefix' but was '${value.message}'"
        )
    }
}
