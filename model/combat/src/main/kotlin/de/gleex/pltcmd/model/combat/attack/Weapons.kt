package de.gleex.pltcmd.model.combat.attack

import de.gleex.pltcmd.util.measure.distance.meters

// some real data:
// - [Historical Probability of Hit (PHit) and Probability of Kill (PKill) Percentages](https://www.alternatewars.com/BBOW/Weapons/Historical_Phits_Pkills.htm)
// - [USMC Weapons Knowledge](http://www.bu.edu/nrotc/semperfi/gouge/Weapon[1].htm)
// - [Equipment of the United States Army](https://military.wikia.org/wiki/Equipment_of_the_United_States_Army)
object Weapons {
    val none = WeaponStats(0, Precision.at100m(Int.MAX_VALUE.meters))
    val pistol = WeaponStats(8, Precision.at100m(1.5.meters))
    val assaultRifle = WeaponStats(12, Precision.at100m(0.6.meters))
    val sniperRifle = WeaponStats(3, Precision.at100m(0.3.meters))

    // 7.62x51mm
    val mmg = WeaponStats(21, Precision.at100m(3.meters)) // A 6-9 burst has approx. 0.7 probability to hit at 300 m
    // .50 cal
    val hmg = WeaponStats(25, Precision.at100m(2.meters))

    val rpg = WeaponStats(1, Precision.at100m(0.6.meters))

    // 105 mm L7 main gun, HEAT rounds: Probability of Hit is 0.69 at 1000 m
    val tankGun = WeaponStats(1, Precision.at100m(0.45.meters))
}
