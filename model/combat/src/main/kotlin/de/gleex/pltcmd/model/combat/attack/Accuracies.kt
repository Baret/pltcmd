package de.gleex.pltcmd.model.combat.attack

/**
 * Holding [Precision] values for common use cases. The values are not only that of the weapon and ammunition alone but
 * also the human factor to handle the weapon. Therefore it can also be used for weapon systems like a main battle tank.
 * The value describes the spread of such a system when shooting consecutively at an aimed for target in good position.
 */
object Accuracies {
    // TODO use `when (Weapons)` after merging issue #112
    val pistol = Precision(20)
    val assaultRifle = Precision(10)
    val sniperRifle = Precision(2)
}
