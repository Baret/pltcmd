package de.gleex.pltcmd.model.elements.reworked

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.util.namegeneration.AlphabetPicker
import de.gleex.pltcmd.util.namegeneration.Namegenerator
import org.hexworks.cobalt.databinding.api.collection.ObservableSet

/**
 * This class is used to provide call signs for a commanding element and all its subordinates.
 * It picks a random (hopefully) unique call sign at creation time but it may be overwritten i.e.
 * by user input.
 */
class CallSignProvider(
        private val corps: Corps,
        private val kind: ElementKind,
        private val rung: Rung,
        subordinates: ObservableSet<Element>
) {

    // TODO: Pick name generator based on corps, kind and rung
    private val generator: Namegenerator = when(corps) {
        Corps.Fighting -> AlphabetPicker() // Cool names
        Corps.Logistics -> AlphabetPicker() // Animal names
        // something like that...
        else            -> AlphabetPicker()
    }

    private var callSign: CallSign = CallSign(generator.generate())

    private val subCallSignProvider = SubCallSignProvider({callSign}, subordinates)

    /**
     * Gets the current [CallSign].
     */
    fun get() = callSign

    /**
     * Gets the [CallSign] for the given subordinate.
     *
     * @see SubCallSignProvider
     */
    fun getFor(subordinate: Element) = subCallSignProvider.callSignFor(subordinate)

    /**
     * Sets the current [CallSign] to the given value.
     */
    fun set(newCallSign: String) = set(CallSign(newCallSign))

    /**
     * Sets the current [CallSign] to the given value.
     */
    fun set(newCallSign: CallSign) {
        callSign = newCallSign
    }
}