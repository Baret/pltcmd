package de.gleex.pltcmd.model.elements

import de.gleex.pltcmd.util.namegeneration.AlphabetPicker
import mu.KotlinLogging
import kotlin.reflect.KProperty

private val log = KotlinLogging.logger {}

/**
 * This class is used to provide call signs for a commanding element and all its subordinates.
 * It picks a random (hopefully) unique call sign at creation time but it may be overwritten i.e.
 * by user input.
 */
internal class CallSignProvider(
    corps: Corps,
    @Suppress("UNUSED_PARAMETER")
    kind: ElementKind,
    @Suppress("UNUSED_PARAMETER")
    rung: Rung
) {

    private var callSign: CallSign

    init {
        // TODO: Pick name generator based on corps, kind and rung (#84)
        callSign = CallSign(
            when (corps) {
                Corps.Fighting  -> AlphabetPicker() // Cool names
                Corps.Logistics -> AlphabetPicker() // Animal names
                // something like that...
                else            -> AlphabetPicker()
            }.generate()
        )
    }

    operator fun getValue(commandingElement: CommandingElement, property: KProperty<*>): CallSign = callSign

    operator fun setValue(commandingElement: CommandingElement, property: KProperty<*>, callSign: CallSign) {
        log.debug { "${this.callSign} is now know as $callSign" }
        this.callSign = callSign
    }
}