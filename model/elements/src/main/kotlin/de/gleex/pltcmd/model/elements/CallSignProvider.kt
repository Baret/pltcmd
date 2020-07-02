package de.gleex.pltcmd.model.elements

import de.gleex.pltcmd.util.namegeneration.AlphabetPicker
import de.gleex.pltcmd.util.namegeneration.Namegenerator
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.reflect.KProperty

/**
 * This class is used to provide call signs for a commanding element and all its subordinates.
 * It picks a random (hopefully) unique call sign at creation time but it may be overwritten i.e.
 * by user input.
 */
internal class CallSignProvider(
        corps: Corps,
        kind: ElementKind,
        rung: Rung
) {

    companion object {
        private val log = LoggerFactory.getLogger(CallSignProvider::class)
    }

    // TODO: Pick name generator based on corps, kind and rung
    private val generator: Namegenerator = when(corps) {
        Corps.Fighting  -> AlphabetPicker() // Cool names
        Corps.Logistics -> AlphabetPicker() // Animal names
        // something like that...
        else            -> AlphabetPicker()
    }

    private var callSign: CallSign = CallSign(generator.generate())

    operator fun getValue(commandingElement: CommandingElement, property: KProperty<*>): CallSign = callSign

    operator fun setValue(commandingElement: CommandingElement, property: KProperty<*>, callSign: CallSign) {
        log.debug("${this.callSign} is now know as $callSign")
        this.callSign = callSign
    }
}