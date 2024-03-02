package de.gleex.pltcmd.model.elements

import de.gleex.kng.generators.nextAsString
import de.gleex.pltcmd.util.namegeneration.angelNames
import de.gleex.pltcmd.util.namegeneration.bluntToolAndWeaponNamesNumbered
import de.gleex.pltcmd.util.namegeneration.heavyAnimalsNumbered
import de.gleex.pltcmd.util.namegeneration.natoAlphabetGenerator
import mu.KotlinLogging
import kotlin.reflect.KProperty

private val log = KotlinLogging.logger {}

/**
 * This class is used to provide call signs for a commanding element and all its subordinates.
 * It picks a random (hopefully) unique call sign at creation time, but it may be overwritten i.e.
 * by user input.
 */
internal class CallSignProvider(
    corps: Corps,
    kind: ElementKind,
    @Suppress("UNUSED_PARAMETER")
    rung: Rung
) {

    private var callSign: CallSign

    init {
        // TODO: Pick name generator based on corps, kind and rung (#84)
        callSign = CallSign(
            when (corps) {
                Corps.Fighting  -> when(kind) {
                    ElementKind.Aerial -> natoAlphabetGenerator // snakeNamesNumbered
                    else -> bluntToolAndWeaponNamesNumbered
                }
                Corps.Logistics -> heavyAnimalsNumbered
                Corps.CombatSupport -> when(rung) {
                    Rung.Company, Rung.Battalion -> angelNames
                    else -> natoAlphabetGenerator // usefulToolsNumbered
                }
                Corps.Reconnaissance -> natoAlphabetGenerator // sneakyNamesNumbered
            }.nextAsString()
        )
    }

    operator fun getValue(commandingElement: CommandingElement, property: KProperty<*>): CallSign = callSign

    operator fun setValue(commandingElement: CommandingElement, property: KProperty<*>, callSign: CallSign) {
        log.debug { "${this.callSign} is now know as $callSign" }
        this.callSign = callSign
    }
}